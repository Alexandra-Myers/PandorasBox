/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.block;

import ivorius.pandorasbox.init.Registry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;

/**
 * Created by lukas on 15.04.14.
 */
public class PandorasBoxBlockEntity extends TileEntity
{
    private float partialRotationYaw;

    public PandorasBoxBlockEntity() {
        super(Registry.TEPB.get());
    }

    public static float rotationFromFacing(Direction facing)
    {
        switch (facing)
        {
            case SOUTH:
                return 0.0f;
            case WEST:
                return 90.0f;
            case NORTH:
                return 180.0f;
            case EAST:
                return 270.0f;
        }

        throw new IllegalArgumentException();
    }

    public void setPartialRotationYaw(float partialRotationYaw)
    {
        this.partialRotationYaw = partialRotationYaw;
    }

    public float getPartialRotationYaw()
    {
        return partialRotationYaw;
    }

    public float getBaseRotationYaw()
    {
        return rotationFromFacing(this.level.getBlockState(this.worldPosition).getValue(PandorasBoxBlock.DIRECTION));
    }

    public float getRotationYaw()
    {
        return getBaseRotationYaw() + getPartialRotationYaw(); // TODO Block model doesn't support gradual rotation yet
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        compoundNBT.putFloat("boxRotationYaw", partialRotationYaw);
        return super.save(compoundNBT);
    }

    @Override
    public void load(BlockState state, CompoundNBT compoundNBT) {
        partialRotationYaw = compoundNBT.getFloat("boxRotationYaw");
        super.load(state, compoundNBT);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        load(level.getBlockState(pkt.getPos()), pkt.getTag());
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        CompoundNBT compound = new CompoundNBT();
        save(compound);
        return new SUpdateTileEntityPacket(worldPosition, 1, compound);
    }
}
