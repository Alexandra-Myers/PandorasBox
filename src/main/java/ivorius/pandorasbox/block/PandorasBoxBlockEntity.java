/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.block;

import ivorius.pandorasbox.init.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Created by lukas on 15.04.14.
 */
public class PandorasBoxBlockEntity extends BlockEntity {
    private float rotationYaw;

    public PandorasBoxBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(Registry.TEPB.get(), p_155229_, p_155230_);
    }

    public void setRotationYaw(float rotationYaw) {
        this.rotationYaw = Mth.wrapDegrees(rotationYaw);
    }

    public float getRotationYaw() {
        return rotationYaw;
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        compoundTag.putFloat("boxRotationYaw", rotationYaw);
        super.saveAdditional(compoundTag);
    }

    @Override
    public void load(CompoundTag compoundNBT) {
        rotationYaw = compoundNBT.getFloat("boxRotationYaw");
        super.load(compoundNBT);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag compoundTag = super.getUpdateTag();
        saveAdditional(compoundTag);
        return compoundTag;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
