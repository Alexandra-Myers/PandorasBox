/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectRandomExplosions extends PBEffectPositionBased
{
    public float minExplosionStrength;
    public float maxExplosionStrength;
    public boolean isFlaming;
    public boolean isSmoking;

    public PBEffectRandomExplosions(int time, int number, double range, float minExplosionStrength, float maxExplosionStrength, boolean isFlaming, boolean isSmoking)
    {
        super(time, number, range);
        this.minExplosionStrength = minExplosionStrength;
        this.maxExplosionStrength = maxExplosionStrength;
        this.isFlaming = isFlaming;
        this.isSmoking = isSmoking;
    }

    @Override
    public void doEffect(World world, PandorasBoxEntity entity, Random random, float newRatio, float prevRatio, double x, double y, double z)
    {
        if (world instanceof ServerWorld)
        {
            world.explode(entity, x, y, z, minExplosionStrength + random.nextFloat() * (maxExplosionStrength - minExplosionStrength), isFlaming, Explosion.Mode.BREAK);
        }
    }

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);

        compound.putFloat("minExplosionStrength", minExplosionStrength);
        compound.putFloat("maxExplosionStrength", maxExplosionStrength);

        compound.putBoolean("isFlaming", isFlaming);
        compound.putBoolean("isSmoking", isSmoking);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);

        minExplosionStrength = compound.getFloat("minExplosionStrength");
        maxExplosionStrength = compound.getFloat("maxExplosionStrength");

        isFlaming = compound.getBoolean("isFlaming");
        isSmoking = compound.getBoolean("isSmoking");
    }
}
