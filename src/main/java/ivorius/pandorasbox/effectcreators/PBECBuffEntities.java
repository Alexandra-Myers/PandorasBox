/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectEntityBased;
import ivorius.pandorasbox.effects.entity.BuffEntityEffect;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.weighted.WeightedSelector;
import ivorius.pandorasbox.weighted.WeightedPotion;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECBuffEntities(IValue time, IValue number, DValue range, float chanceForMoreEffects, List<WeightedPotion> applicablePotions) implements PBEffectCreator {
    public static final MapCodec<PBECBuffEntities> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("time").forGetter(PBECBuffEntities::time),
                            IValue.CODEC.fieldOf("number").forGetter(PBECBuffEntities::number),
                            DValue.CODEC.fieldOf("range").forGetter(PBECBuffEntities::range),
                            ExtraCodecs.floatRange(0, 1).fieldOf("chance_for_more_effects").forGetter(PBECBuffEntities::chanceForMoreEffects),
                            WeightedPotion.CODEC.listOf().fieldOf("mob_effects").forGetter(PBECBuffEntities::applicablePotions))
                    .apply(instance, PBECBuffEntities::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int number = this.number.getValue(random);
        int time = this.time.getValue(random);
        double range = this.range.getValue(random);

        List<MobEffectInstance> effects = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            WeightedPotion weightedPotion = WeightedSelector.selectItem(random, applicablePotions);

            effects.addAll(weightedPotion.build(random));
        }

        return new PBEffectEntityBased(time, range, new BuffEntityEffect(effects.toArray(new MobEffectInstance[0])));
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return chanceForMoreEffects;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
