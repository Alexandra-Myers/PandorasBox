/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 04.04.14.
 */
public record ZConstant(boolean value) implements ZValue
{
    public static final MapCodec<ZConstant> CODEC = Codec.BOOL.xmap(ZConstant::new, ZConstant::value).fieldOf("guaranteed");

    @Override
    public boolean getValue(RandomSource random)
    {
        return value;
    }

    @Override
    public @NotNull MapCodec<? extends ZValue> codec() {
        return CODEC;
    }
}
