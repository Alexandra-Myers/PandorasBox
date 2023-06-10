/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.level.Level;
import net.minecraft.world.storage.ServerWorldInfo;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectSetTime extends PBEffectNormal
{
    public int totalPlus;
    public PBEffectSetTime() {}

    public PBEffectSetTime(int maxTicksAlive, int totalPlus)
    {
        super(maxTicksAlive);
        this.totalPlus = totalPlus;
    }

    @Override
    public void doEffect(Level world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, float prevRatio, float newRatio)
    {
        int newPlus = MathHelper.floor(totalPlus * newRatio);
        int prevPlus = MathHelper.floor(totalPlus * prevRatio);
        int plus = newPlus - prevPlus;

        if (world.getLevelData() instanceof ServerWorldInfo) {
            ((ServerWorldInfo) world.getLevelData()).setGameTime(world.getGameTime() + plus);
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);

        compound.putInt("totalPlus", totalPlus);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        totalPlus = compound.getInt("totalPlus");
    }
}
