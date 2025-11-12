package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectEntityBased;
import ivorius.pandorasbox.effects.entity.FakeDeathEffect;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record PBECFakeDeath(IValue time, DValue range, float chanceForMoreEffects) implements PBEffectCreator {
    public static final MapCodec<PBECFakeDeath> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(IValue.CODEC.fieldOf("time").forGetter(PBECFakeDeath::time),
                        DValue.CODEC.fieldOf("range").forGetter(PBECFakeDeath::range),
                        Codec.floatRange(0, 1).fieldOf("chance_for_more_effects").forGetter(PBECFakeDeath::chanceForMoreEffects))
                .apply(instance, PBECFakeDeath::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int time = this.time.getValue(random);
        double range = this.range.getValue(random);

        return new PBEffectEntityBased(time, range, new FakeDeathEffect());
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
