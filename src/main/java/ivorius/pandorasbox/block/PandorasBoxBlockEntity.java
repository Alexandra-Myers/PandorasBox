/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.block;

import ivorius.pandorasbox.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 15.04.14.
 */
public class PandorasBoxBlockEntity extends BlockEntity {
    private float rotationYaw;

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
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.putFloat("boxRotationYaw", rotationYaw);
        super.saveAdditional(compoundTag, provider);
    }

    @Override
    public void loadAdditional(CompoundTag compoundNBT, HolderLookup.Provider provider) {
        rotationYaw = compoundNBT.getFloat("boxRotationYaw");
        super.loadAdditional(compoundNBT, provider);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag compoundTag = super.getUpdateTag(provider);
        saveAdditional(compoundTag, provider);
        return compoundTag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
