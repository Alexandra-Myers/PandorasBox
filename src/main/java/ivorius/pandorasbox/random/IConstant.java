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
public record IConstant(int constant) implements IValue {
    public static final MapCodec<IConstant> CODEC = Codec.INT.xmap(IConstant::new, IConstant::constant).fieldOf("value");

    @Override
    public int getValue(RandomSource random) {
        return constant;
    }

    @Override
    public @NotNull MapCodec<? extends IValue> codec() {
        return CODEC;
    }
}
