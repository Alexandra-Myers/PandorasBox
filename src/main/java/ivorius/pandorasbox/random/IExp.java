/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 04.04.14.
 */
public record IExp(int min, int max, double exp) implements IValue {
    public static final MapCodec<IExp> CODEC = RecordCodecBuilder.<IExp>mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("min_value").forGetter(IExp::min),
                            Codec.INT.fieldOf("max_value").forGetter(IExp::max),
                            Codec.DOUBLE.fieldOf("base").forGetter(IExp::exp))
                    .apply(instance, IExp::new)).validate(iExp -> {
                        if (iExp.min > iExp.max) return DataResult.error(() -> "Constraints for exponential random mismatched, min greater than max!");
                        else return DataResult.success(iExp);
    });

    @Override
    public int getValue(RandomSource random) {
        return (int) Math.round(min + ((Math.pow(exp, random.nextDouble()) - 1.0) / (exp - 1.0)) * (max - min));
    }

    @Override
    public @NotNull MapCodec<? extends IValue> codec() {
        return CODEC;
    }
}
