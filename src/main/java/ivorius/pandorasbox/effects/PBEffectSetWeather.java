/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ServerLevelData;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectSetWeather extends PBEffectNormal
{
    public boolean rain;
    public boolean thunder;
    public int rainTime;
    public PBEffectSetWeather() {}

    public PBEffectSetWeather(int maxTicksAlive, boolean rain, boolean thunder, int rainTime)
    {
        super(maxTicksAlive);
        this.rain = rain;
        this.thunder = thunder;
        this.rainTime = rainTime;
    }

    @Override
    public void doEffect(Level world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, float prevRatio, float newRatio)
    {
    }

    @Override
    public void finalizeEffect(Level world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random)
    {
        if (world.getLevelData() instanceof ServerLevelData worldInfo) {
            worldInfo.setRainTime(rainTime);
            worldInfo.setThunderTime(rainTime);
            worldInfo.setRaining(rain);
            worldInfo.setThundering(rain && thunder);
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);

        compound.putInt("rainTime", rainTime);
        compound.putBoolean("rain", rain);
        compound.putBoolean("thunder", thunder);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        rainTime = compound.getInt("rainTime");
        rain = compound.getBoolean("rain");
        thunder = compound.getBoolean("thunder");
    }
}
