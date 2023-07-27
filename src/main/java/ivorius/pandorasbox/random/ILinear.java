/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.random;

import net.minecraft.util.RandomSource;

/**
 * Created by lukas on 04.04.14.
 */
public class ILinear implements IValue
{
    public int min;
    public int max;

    public ILinear(int min, int max)
    {
        this.min = min;
        this.max = max;
    }

    @Override
    public int getValue(RandomSource random)
    {
        return min + random.nextInt(max - min + 1);
    }
}
