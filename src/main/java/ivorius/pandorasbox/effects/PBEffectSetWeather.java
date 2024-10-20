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
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectSetWeather extends PBEffectNormal {
    public boolean rain;
    public boolean thunder;
    public int rainTime;
    public PBEffectSetWeather() {}

    public PBEffectSetWeather(int maxTicksAlive, boolean rain, boolean thunder, int rainTime) {
        super(maxTicksAlive);
        this.rain = rain;
        this.thunder = thunder;
        this.rainTime = rainTime;
    }

    @Override
    public void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {
    }

    @Override
    public void finalizeEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random) {
        if (level instanceof ServerLevel serverLevel) {
            if (serverLevel.isRaining() && !rain) {
                serverLevel.setWeatherParameters(rainTime, 0, false, false);
            }
            serverLevel.setWeatherParameters(0, rainTime, rain, thunder);
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.writeToNBT(compound, registryAccess);

        compound.putInt("rainTime", rainTime);
        compound.putBoolean("rain", rain);
        compound.putBoolean("thunder", thunder);
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.readFromNBT(compound, registryAccess);

        rainTime = compound.getInt("rainTime");
        rain = compound.getBoolean("rain");
        thunder = compound.getBoolean("thunder");
    }
}
