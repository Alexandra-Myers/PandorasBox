/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 30.03.14.
 */
public abstract class PBEffectPositionBased extends PBEffectNormal {
    public int number;

    public double range;
    public PBEffectPositionBased() {}

    public PBEffectPositionBased(int time, int number, double range) {
        super(time);

        this.number = number;
        this.range = range;
    }

    @Override
    public void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {
        if (level instanceof ServerLevel serverLevel) {
            int prev = getSpawnNumber(prevRatio);
            int toSpawn = getSpawnNumber(newRatio) - prev;

            for (int i = 0; i < toSpawn; i++) {
                double eX = effectCenter.x + (random.nextDouble() - random.nextDouble()) * range;
                double eY = effectCenter.y + (random.nextDouble() - random.nextDouble()) * 3.0 * 2.0;
                double eZ = effectCenter.z + (random.nextDouble() - random.nextDouble()) * range;

                doEffect(serverLevel, entity, random, newRatio, prevRatio, eX, eY, eZ);
            }
        }
    }

    public abstract void doEffect(ServerLevel serverLevel, PandorasBoxEntity entity, RandomSource random, float newRatio, float prevRatio, double x, double y, double z);

    private int getSpawnNumber(float ratio) {
        return Mth.floor(ratio * number);
    }

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.writeToNBT(compound, registryAccess);

        compound.putInt("number", number);
        compound.putDouble("range", range);
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.readFromNBT(compound, registryAccess);

        number = compound.getInt("number");
        range = compound.getDouble("range");
    }
}
