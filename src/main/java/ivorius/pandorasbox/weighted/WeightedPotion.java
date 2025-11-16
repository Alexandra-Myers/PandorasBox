/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.weighted;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.random.IValue;
import net.atlas.atlascore.util.Codecs;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.List;

/**
 * Created by lukas on 31.03.14.
 */
public record WeightedPotion(double weight, HolderSet<MobEffect> toApply, IValue amplifier, IValue duration) implements WeightedSelector.Item {
    public static final Codec<WeightedPotion> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codecs.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").forGetter(WeightedPotion::weight),
                            RegistryCodecs.homogeneousList(Registries.MOB_EFFECT).fieldOf("to_apply").forGetter(WeightedPotion::toApply),
                            IValue.CODEC.fieldOf("amplifier").forGetter(WeightedPotion::amplifier),
                            IValue.CODEC.fieldOf("duration").forGetter(WeightedPotion::duration))
                    .apply(instance, WeightedPotion::new)
    );

    @Override
    public double weight()
    {
        return weight;
    }

    public List<MobEffectInstance> build(RandomSource random) {
        return toApply.stream().map(mobEffectHolder -> new MobEffectInstance(mobEffectHolder.value(), duration().getValue(random), amplifier().getValue(random), false, false)).toList();
    }
}
