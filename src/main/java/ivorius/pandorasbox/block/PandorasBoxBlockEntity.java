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
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
    protected void applyImplicitComponents(DataComponentGetter dataComponentGetter) {
        super.applyImplicitComponents(dataComponentGetter);
        setEnchantments(dataComponentGetter.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY));
        setEffectComponent(dataComponentGetter.getOrDefault(ComponentInit.EFFECT_COMPONENT, PBEffectComponent.DEFAULT));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        if (!enchantments.isEmpty()) builder.set(DataComponents.ENCHANTMENTS, enchantments);
        if (!effectComponent.isEmpty()) builder.set(ComponentInit.EFFECT_COMPONENT, effectComponent);
    }

    @Override
    protected void saveAdditional(ValueOutput valueOutput) {
        valueOutput.putFloat("boxRotationYaw", rotationYaw);
        valueOutput.store("enchantments", ItemEnchantments.CODEC, enchantments);
        valueOutput.store("effect_holders", PBEffectComponent.CODEC, effectComponent);
        super.saveAdditional(valueOutput);
    }

    @Override
    public void loadAdditional(ValueInput valueInput) {
        rotationYaw = valueInput.getFloatOr("boxRotationYaw", 0);
        enchantments = valueInput.read("enchantments", ItemEnchantments.CODEC).orElse(ItemEnchantments.EMPTY);
        effectComponent = valueInput.read("effect_holders", PBEffectComponent.CODEC).orElse(PBEffectComponent.DEFAULT);
        super.loadAdditional(valueInput);
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
