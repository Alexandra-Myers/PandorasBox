/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effectholder.EffectHolder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectDuplicateBox;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECDuplicateBox(IValue spawnMode, DValue moreEffectChance, HolderSet<EffectHolder> includedEffectHolders) implements PBEffectCreator {
    public static final MapCodec<PBECDuplicateBox> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("spawn_mode").forGetter(PBECDuplicateBox::spawnMode),
                            DValue.CODEC.fieldOf("more_effect_chance").forGetter(PBECDuplicateBox::moreEffectChance),
                            EffectHolder.CODEC.optionalFieldOf("included_effect_holders", HolderSet.empty()).forGetter(PBECDuplicateBox::includedEffectHolders))
                    .apply(instance, PBECDuplicateBox::new));
    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int spawnMode = this.spawnMode.getValue(random);
        return new PBEffectDuplicateBox(spawnMode, includedEffectHolders);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return (float) moreEffectChance.getValue(random);
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
