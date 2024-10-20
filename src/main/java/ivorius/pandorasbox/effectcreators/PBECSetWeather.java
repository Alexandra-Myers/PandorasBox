/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectSetWeather;
import ivorius.pandorasbox.random.IValue;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECSetWeather implements PBEffectCreator
{
    public IValue weather;
    public IValue rainTime;
    public IValue delay;

    public PBECSetWeather(IValue weather, IValue rainTime, IValue delay)
    {
        this.weather = weather;
        this.rainTime = rainTime;
        this.delay = delay;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        int time = this.delay.getValue(random);
        int weather = this.weather.getValue(random);
        int rainTime = this.rainTime.getValue(random);

        return new PBEffectSetWeather(time, weather > 0, weather > 1, rainTime);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.8f;
    }
}
