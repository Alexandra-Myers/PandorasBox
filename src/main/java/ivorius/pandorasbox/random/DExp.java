/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.random;

import net.minecraft.util.RandomSource;

/**
 * Created by lukas on 04.04.14.
 */
public class DExp implements DValue
{
    public double min;
    public double max;

    public double exp;

    public DExp(double min, double max, double exp)
    {
        this.min = min;
        this.max = max;
        this.exp = exp;
    }

    @Override
    public double getValue(RandomSource random)
    {
        return min + ((Math.pow(exp, random.nextDouble()) - 1.0) / (exp - 1.0)) * (max - min);
    }
}
