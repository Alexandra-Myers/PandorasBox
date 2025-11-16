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
public record IFlags(int minFlags, Integer[] flags, Double[] chances) implements IValue {
    public static final MapCodec<IFlags> CODEC = validate(RecordCodecBuilder.<IFlags>mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("min_flags").forGetter(IFlags::minFlags),
                        PBNBTHelper.arrayCodec(Codec.INT, () -> new Integer[0]).fieldOf("flags").forGetter(IFlags::flags),
                        PBNBTHelper.arrayCodec(Codec.DOUBLE, () -> new Double[0]).fieldOf("chances").forGetter(IFlags::chances))
                    .apply(instance, IFlags::new)), iFlags -> {
                        if (iFlags.flags.length != iFlags.chances.length) return DataResult.error(() -> "Misaligned flags and chances!");
                        else return DataResult.success(iFlags);
    });

    public IFlags(int minFlags, Object... flagsWithChances) {
        this(minFlags, new Integer[flagsWithChances.length / 2], new Double[flagsWithChances.length / 2]);

        for (int i = 0; i < flags.length; i++) {
            flags[i] = (Integer) flagsWithChances[i * 2];
            chances[i] = (Double) flagsWithChances[i * 2 + 1];
        }
    }

    @Override
    public int getValue(RandomSource random) {
        int value = 0;
        int flagsSet = 0;

        for (int i = 0; i < flags.length; i++) {
            if (random.nextDouble() < chances[i]) {
                value |= 1 << flags[i];
                flagsSet++;
            }
        }

        while (flagsSet < minFlags) {
            int[] values = new int[flags.length - flagsSet];
            double[] weights = new double[values.length];

            int currentIndex = 0;
            for (int i = 0; i < values.length; i++) {
                while ((flagsSet & (1 << flags[currentIndex])) > 0) {
                    currentIndex++;
                }

                values[i] = flags[currentIndex];
                weights[i] = chances[currentIndex];
                currentIndex++;
            }

            value |= 1 << getRandomValue(random, values, weights);
            flagsSet++;
        }

        return value;
    }

    @Override
    public @NotNull MapCodec<? extends IValue> codec() {
        return CODEC;
    }

    public static int getRandomValue(RandomSource random, int[] values, double[] weights) {
        double total = 0.0;
        for (double weight : weights) {
            total += weight;
        }

        double selected = random.nextDouble() * total;

        for (int i = 0; i < weights.length; i++) {
            selected -= weights[i];
            if (selected < 0.0) {
                return values[i];
            }
        }

        throw new RuntimeException("Weights have invalid values!");
    }
}
