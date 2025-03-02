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

/**
 * Created by lukas on 04.04.14.
 */
public record IWeighted(Integer[] values, Integer[] weights) implements IValue {
    public static final MapCodec<IWeighted> CODEC = RecordCodecBuilder.<IWeighted>mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(Codec.INT, () -> new Integer[0]).fieldOf("values").forGetter(IWeighted::values),
                            PBNBTHelper.arrayCodec(Codec.INT, () -> new Integer[0]).fieldOf("weights").forGetter(IWeighted::weights))
                    .apply(instance, IWeighted::new)).validate(iWeighted -> {
                        if (iWeighted.values.length != iWeighted.weights.length) return DataResult.error(() -> "Weighted value provided without aligned values and weights!");
                        else return DataResult.success(iWeighted);
    });

    public IWeighted(int... valuesWithWeights) {
        this(new Integer[valuesWithWeights.length / 2], new Integer[valuesWithWeights.length / 2]);

        for (int i = 0; i < values.length; i++) {
            values[i] = valuesWithWeights[i * 2];
            weights[i] = valuesWithWeights[i * 2 + 1];
        }
    }

    @Override
    public int getValue(RandomSource random) {
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
    public @NotNull MapCodec<? extends IValue> codec() {
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
