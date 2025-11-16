package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effectholder.EffectHolder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectMeltdown;
import ivorius.pandorasbox.init.Init;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import net.minecraft.core.HolderSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by Alexandra on 18.10.24.
 */
public record PBECMeltdown(DValue range, IValue maxTicksAlive, HolderSet<EffectHolder> includedEffectHolders) implements PBEffectCreator {
    public static final MapCodec<PBECMeltdown> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(DValue.CODEC.fieldOf("range").forGetter(PBECMeltdown::range),
                            IValue.CODEC.fieldOf("max_ticks_alive").forGetter(PBECMeltdown::maxTicksAlive),
                            EffectHolder.MELTDOWN_CODEC.optionalFieldOf("included_meltdown_holders", HolderSet.direct()).forGetter(PBECMeltdown::includedEffectHolders))
                    .apply(instance, PBECMeltdown::new));
    @Override
    public PBEffect constructEffect(Level level, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int maxTicksAlive = this.maxTicksAlive.getValue(random);
        PBEffect pbEffect = PBECRegistry.createRandomEffect(level, random, x, y, z, true, Optional.of(includedEffectHolders), Init.MELTDOWN_EFFECT_HOLDER_REGISTRY_KEY);

        return new PBEffectMeltdown(pbEffect, includedEffectHolders, (float) range, maxTicksAlive);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return 0;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
