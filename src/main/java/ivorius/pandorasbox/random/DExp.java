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
public record DExp(double min, double max, double exp) implements DValue {
    public static final MapCodec<DExp> CODEC = RecordCodecBuilder.<DExp>mapCodec(instance ->
            instance.group(Codec.DOUBLE.fieldOf("min_value").forGetter(DExp::min),
                            Codec.DOUBLE.fieldOf("max_value").forGetter(DExp::max),
                            Codec.DOUBLE.fieldOf("base").forGetter(DExp::exp))
                    .apply(instance, DExp::new)).validate(dExp -> {
        if (dExp.min > dExp.max) return DataResult.error(() -> "Constraints for exponential random mismatched, min greater than max!");
        else return DataResult.success(dExp);
    });

    @Override
    public double getValue(RandomSource random) {
        return min + ((Math.pow(exp, random.nextDouble()) - 1.0) / (exp - 1.0)) * (max - min);
    }

    @Override
    public @NotNull MapCodec<? extends DValue> codec() {
        return CODEC;
    }
}
