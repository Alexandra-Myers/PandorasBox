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
public record DGaussian(double min, double max) implements DValue {
    public static final MapCodec<DGaussian> CODEC = RecordCodecBuilder.<DGaussian>mapCodec(instance ->
            instance.group(Codec.DOUBLE.fieldOf("min").forGetter(DGaussian::min),
                            Codec.DOUBLE.fieldOf("max").forGetter(DGaussian::max))
                    .apply(instance, DGaussian::new)).validate(dGaussian -> {
        if (dGaussian.min > dGaussian.max) return DataResult.error(() -> "Constraints for gaussian mismatched, min greater than max!");
        else return DataResult.success(dGaussian);
    });

    @Override
    public double getValue(RandomSource random) {
        return (min + max * 0.5) + (random.nextDouble() - random.nextDouble()) * (max - min) * 0.5;
    }

    @Override
    public @NotNull MapCodec<? extends DValue> codec() {
        return CODEC;
    }
}
