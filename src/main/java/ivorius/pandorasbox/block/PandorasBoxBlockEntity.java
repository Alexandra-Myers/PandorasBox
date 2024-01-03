/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.block;

import ivorius.pandorasbox.init.BlockEntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 15.04.14.
 */
public class PandorasBoxBlockEntity extends BlockEntity
{
    private float rotationYaw;

    public PandorasBoxBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntityInit.BEPB, p_155229_, p_155230_);
    }

    public static float rotationFromFacing(Direction facing)
    {
        return facing.toYRot();
    }

    public void setRotationYaw(float rotationYaw)
    {
        this.rotationYaw = rotationYaw;
    }

    public float getRotationYaw()
    {
        return rotationYaw;
    }

    public float getBaseRotationYaw()
    {
        BlockState state = this.getBlockState();
        return rotationFromFacing(state.getBlock() instanceof PandorasBoxBlock ? state.getValue(PandorasBoxBlock.DIRECTION) : Direction.NORTH);
    }

    public float getFinalRotationYaw()
    {
        if(getRotationYaw() - getBaseRotationYaw() > 90) return getBaseRotationYaw();
        return getRotationYaw();
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
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag compoundTag = super.getUpdateTag();
        saveAdditional(compoundTag);
        return compoundTag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
