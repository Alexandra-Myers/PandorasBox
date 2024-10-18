/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
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
    public void doEffect(ServerLevel serverLevel, PandorasBoxEntity entity, RandomSource random, float newRatio, float prevRatio, double x, double y, double z) {
        serverLevel.explode(entity, x, y, z, minExplosionStrength + random.nextFloat() * (maxExplosionStrength - minExplosionStrength), isFlaming, Level.ExplosionInteraction.TNT);
    }

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.writeToNBT(compound, registryAccess);

        compound.putFloat("minExplosionStrength", minExplosionStrength);
        compound.putFloat("maxExplosionStrength", maxExplosionStrength);

        compound.putBoolean("isFlaming", isFlaming);
        compound.putBoolean("isSmoking", isSmoking);
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.readFromNBT(compound, registryAccess);

        minExplosionStrength = compound.getFloat("minExplosionStrength");
        maxExplosionStrength = compound.getFloat("maxExplosionStrength");

        isFlaming = compound.getBoolean("isFlaming");
        isSmoking = compound.getBoolean("isSmoking");
    }
}
