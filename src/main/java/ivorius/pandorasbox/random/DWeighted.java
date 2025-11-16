/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.util.ExtraCodecs.validate;

/**
 * Created by lukas on 04.04.14.
 */
public record DWeighted(Double[] values, Integer[] weights) implements DValue {
    public static final MapCodec<DWeighted> CODEC = validate(RecordCodecBuilder.<DWeighted>mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(Codec.DOUBLE, () -> new Double[0]).fieldOf("values").forGetter(DWeighted::values),
                            PBNBTHelper.arrayCodec(Codec.INT, () -> new Integer[0]).fieldOf("weights").forGetter(DWeighted::weights))
                    .apply(instance, DWeighted::new)), dWeighted -> {
        if (dWeighted.values.length != dWeighted.weights.length) return DataResult.error(() -> "Weighted value provided without aligned values and weights!");
        else return DataResult.success(dWeighted);
    });

    @Override
    public double getValue(RandomSource random) {
        int total = getTotalWeight(weights);
        int selected = random.nextInt(total);

        for (int i = 0; i < weights.length; i++) {
            selected -= weights[i];
            if (selected < 0) {
                return values[i];
            }
        }

        throw new RuntimeException("Weights have invalid values!");
    }

    @Override
    public @NotNull MapCodec<? extends DValue> codec() {
        return CODEC;
    }

    public static int getTotalWeight(Integer[] weights) {
        int weight = 0;

        for (int i : weights) {
            weight += i;
        }

        return weight;
    }
}
