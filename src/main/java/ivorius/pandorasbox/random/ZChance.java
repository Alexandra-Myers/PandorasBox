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
public record ZChance(double chance) implements ZValue
{
    public static final MapCodec<ZChance> CODEC = Codec.DOUBLE.xmap(ZChance::new, ZChance::chance).fieldOf("chance");

    @Override
    public boolean getValue(RandomSource random)
    {
        return random.nextDouble() < chance;
    }

    @Override
    public @NotNull MapCodec<? extends ZValue> codec() {
        return CODEC;
    }
}
