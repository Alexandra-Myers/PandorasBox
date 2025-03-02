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
public record DLinear(double min, double max) implements DValue {
    public static final MapCodec<DLinear> CODEC = RecordCodecBuilder.<DLinear>mapCodec(instance ->
            instance.group(Codec.DOUBLE.fieldOf("min").forGetter(DLinear::min),
                            Codec.DOUBLE.fieldOf("max").forGetter(DLinear::max))
                    .apply(instance, DLinear::new)).validate(dLinear -> {
        if (dLinear.min > dLinear.max) return DataResult.error(() -> "Constraints for linear random mismatched, min greater than max!");
        else return DataResult.success(dLinear);
    });

    @Override
    public double getValue(RandomSource random) {
        return min + random.nextDouble() * (max - min);
    }

    @Override
    public @NotNull MapCodec<? extends DValue> codec() {
        return CODEC;
    }
}
