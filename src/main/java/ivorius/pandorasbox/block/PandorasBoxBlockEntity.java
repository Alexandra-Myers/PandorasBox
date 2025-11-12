/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.block;

import ivorius.pandorasbox.component.PBEffectComponent;
import ivorius.pandorasbox.init.BlockEntityInit;
import ivorius.pandorasbox.init.ComponentInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 15.04.14.
 */
public class PandorasBoxBlockEntity extends BlockEntity {
    private float rotationYaw;
    private ItemEnchantments enchantments = ItemEnchantments.EMPTY;
    private PBEffectComponent effectComponent = PBEffectComponent.DEFAULT;

    public PandorasBoxBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntityInit.BEPB, p_155229_, p_155230_);
    }

    public void setRotationYaw(float rotationYaw) {
        this.rotationYaw = Mth.wrapDegrees(rotationYaw);
    }

    public float getRotationYaw()
    {
        return rotationYaw;
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        setEnchantments(componentInput.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));
        setEffectComponent(componentInput.getOrDefault(ComponentInit.EFFECT_COMPONENT, PBEffectComponent.DEFAULT));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        if (!enchantments.isEmpty()) builder.set(DataComponents.ENCHANTMENTS, enchantments);
        if (!effectComponent.isEmpty()) builder.set(ComponentInit.EFFECT_COMPONENT, effectComponent);
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.putFloat("boxRotationYaw", rotationYaw);
        RegistryOps<Tag> registryOps = provider.createSerializationContext(NbtOps.INSTANCE);
        Tag enchantments = ItemEnchantments.CODEC.encodeStart(registryOps, this.enchantments).getOrThrow();
        Tag effectComponent = PBEffectComponent.CODEC.encodeStart(registryOps, this.effectComponent).getOrThrow();
        compoundTag.put("enchantments", enchantments);
        compoundTag.put("effect_holders", effectComponent);
        super.saveAdditional(compoundTag, provider);
    }

    @Override
    public void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        rotationYaw = compoundTag.getFloat("boxRotationYaw");
        Tag readEnchantments = compoundTag.get("enchantments");
        Tag readEffectComponent = compoundTag.get("effect_holders");
        RegistryOps<Tag> registryOps = provider.createSerializationContext(NbtOps.INSTANCE);
        enchantments = ItemEnchantments.CODEC.orElse(ItemEnchantments.EMPTY).parse(registryOps, readEnchantments).getOrThrow();
        effectComponent = PBEffectComponent.CODEC.orElse(PBEffectComponent.DEFAULT).parse(registryOps, readEffectComponent).getOrThrow();
        super.loadAdditional(compoundTag, provider);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveCustomOnly(provider);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public ItemEnchantments getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(ItemEnchantments enchantments) {
        this.enchantments = enchantments;
    }

    public PBEffectComponent getEffectComponent() {
        return effectComponent;
    }

    public void setEffectComponent(PBEffectComponent effectComponent) {
        this.effectComponent = effectComponent;
    }
}
