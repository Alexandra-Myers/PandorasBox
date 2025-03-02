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
public record DConstant(double constant) implements DValue {
    public static final MapCodec<DConstant> CODEC = Codec.DOUBLE.xmap(DConstant::new, DConstant::constant).fieldOf("value");

    @Override
    public double getValue(RandomSource random)
    {
        return constant;
    }

    @Override
    public @NotNull MapCodec<? extends DValue> codec() {
        return CODEC;
    }
}
