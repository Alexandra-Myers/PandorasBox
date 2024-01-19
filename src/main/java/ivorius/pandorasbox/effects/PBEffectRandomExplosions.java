/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectRandomExplosions extends PBEffectPositionBased {
    public float minExplosionStrength;
    public float maxExplosionStrength;
    public boolean isFlaming;
    public boolean isSmoking;
    public PBEffectRandomExplosions() {

    }

    public PBEffectRandomExplosions(int time, int number, double range, float minExplosionStrength, float maxExplosionStrength, boolean isFlaming, boolean isSmoking) {
        super(time, number, range);
        this.minExplosionStrength = minExplosionStrength;
        this.maxExplosionStrength = maxExplosionStrength;
        this.isFlaming = isFlaming;
        this.isSmoking = isSmoking;
    }

    @Override
    public void doEffect(Level world, PandorasBoxEntity entity, RandomSource random, float newRatio, float prevRatio, double x, double y, double z) {
        if (!world.isClientSide) {
            world.explode(entity, x, y, z, minExplosionStrength + random.nextFloat() * (maxExplosionStrength - minExplosionStrength), isFlaming, Level.ExplosionInteraction.TNT);
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);

        compound.putFloat("minExplosionStrength", minExplosionStrength);
        compound.putFloat("maxExplosionStrength", maxExplosionStrength);

        compound.putBoolean("isFlaming", isFlaming);
        compound.putBoolean("isSmoking", isSmoking);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        minExplosionStrength = compound.getFloat("minExplosionStrength");
        maxExplosionStrength = compound.getFloat("maxExplosionStrength");

        isFlaming = compound.getBoolean("isFlaming");
        isSmoking = compound.getBoolean("isSmoking");
    }
}
