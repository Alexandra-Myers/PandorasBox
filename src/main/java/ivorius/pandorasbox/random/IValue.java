/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.random;

import net.minecraft.util.RandomSource;

/**
 * Created by lukas on 04.04.14.
 */
public interface IValue
{
    public int getValue(RandomSource random);
}
