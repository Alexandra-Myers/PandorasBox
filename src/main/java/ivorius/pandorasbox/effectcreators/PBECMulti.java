/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectMulti;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECMulti(PBEffectCreator[] effects, Integer[] delays) implements PBEffectCreator {
    public static final MapCodec<PBECMulti> CODEC = RecordCodecBuilder.<PBECMulti>mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(PBEffectCreator.CODEC, () -> new PBEffectCreator[0]).fieldOf("effects").forGetter(PBECMulti::effects),
                            PBNBTHelper.arrayCodec(ExtraCodecs.NON_NEGATIVE_INT, () -> new Integer[0]).fieldOf("delays").forGetter(PBECMulti::delays))
                    .apply(instance, PBECMulti::new)).validate(pbecMulti -> {
                        if (pbecMulti.effects.length != pbecMulti.delays.length) return DataResult.error(() -> "Misaligned effects and delays in multi-effect creator!");
                        else return DataResult.success(pbecMulti);
    });
    public static PBECMulti create(Object... effectsAndDelays) {
        PBECMulti multi = new PBECMulti(new PBEffectCreator[effectsAndDelays.length / 2], new Integer[effectsAndDelays.length / 2]);
        if (effectsAndDelays.length % 2 != 0) throw new IllegalStateException("Each effect must be mapped to a delay!");

        for (int i = 0; i < multi.effects.length; i++) {
            multi.effects[i] = (PBEffectCreator) effectsAndDelays[i * 2];
            multi.delays[i] = (Integer) effectsAndDelays[i * 2 + 1];
        }
        return multi;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        PBEffect[] createdEffects = new PBEffect[effects.length];

        for (int i = 0; i < effects.length; i++) {
            createdEffects[i] = effects[i].constructEffect(world, x, y, z, random);
        }
        int[] newDelays = new int[delays.length];
        for (int i = 0; i < delays.length; i++) {
            newDelays[i] = delays[i];
        }

        return new PBEffectMulti(createdEffects, newDelays);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        float min = 999;
        for (PBEffectCreator effect : effects) {
            min = Math.min(effect.chanceForMoreEffects(world, x, y, z, random), min);
        }
        return min;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
