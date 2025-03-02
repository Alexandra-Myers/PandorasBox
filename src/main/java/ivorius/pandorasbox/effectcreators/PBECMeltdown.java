package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectMeltdown;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import static ivorius.pandorasbox.effects.PBEffects.MELTDOWN_CREATORS;

/**
 * Created by Alexandra on 18.10.24.
 */
public record PBECMeltdown(DValue range, IValue maxTicksAlive) implements PBEffectCreator {
    public static final MapCodec<PBECMeltdown> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(DValue.CODEC.fieldOf("range").forGetter(PBECMeltdown::range),
                            IValue.CODEC.fieldOf("max_ticks_alive").forGetter(PBECMeltdown::maxTicksAlive))
                    .apply(instance, PBECMeltdown::new));
    @Override
    public PBEffect constructEffect(Level level, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int maxTicksAlive = this.maxTicksAlive.getValue(random);
        int rand = random.nextInt(MELTDOWN_CREATORS.length);
        PBEffect pbEffect = MELTDOWN_CREATORS[rand].constructEffect(level, x, y, z, random);

        return new PBEffectMeltdown(pbEffect, (float) range, maxTicksAlive);
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
