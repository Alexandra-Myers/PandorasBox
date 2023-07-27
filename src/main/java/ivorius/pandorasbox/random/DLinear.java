/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.random;

import net.minecraft.util.RandomSource;

/**
 * Created by lukas on 04.04.14.
 */
public class DLinear implements DValue
{
    public double min;
    public double max;

    public DLinear(double min, double max)
    {
        this.min = min;
        this.max = max;
    }

    @Override
    public double getValue(RandomSource random)
    {
        return min + random.nextDouble() * (max - min);
    }
}
