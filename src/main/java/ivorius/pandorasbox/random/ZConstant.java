/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.random;

import net.minecraft.util.RandomSource;

/**
 * Created by lukas on 04.04.14.
 */
public class ZConstant implements ZValue
{
    public boolean value;

    public ZConstant(boolean value)
    {
        this.value = value;
    }

    @Override
    public boolean getValue(RandomSource random)
    {
        return value;
    }
}
