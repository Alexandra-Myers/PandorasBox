package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effectcreators.generate.GenerateEffectCreator;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenerate;
import ivorius.pandorasbox.random.DValue;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record PBECGenerate(DValue range, float chanceForMoreEffects, GenerateEffectCreator generateEffectCreator) implements PBEffectCreator {
    public static final MapCodec<PBECGenerate> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(DValue.CODEC.fieldOf("range").forGetter(PBECGenerate::range),
                            Codec.floatRange(0, 1).fieldOf("chance_for_more_effects").forGetter(PBECGenerate::chanceForMoreEffects),
                            GenerateEffectCreator.CODEC.fieldOf("generate_effect").forGetter(PBECGenerate::generateEffectCreator))
                    .apply(instance, PBECGenerate::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);
        int passes = generateEffectCreator.getPasses();

        return new PBEffectGenerate(time, range, passes, PandorasBoxHelper.getRandomUnifiedSeed(random), generateEffectCreator.constructGenerate(world, x, y, z, random));
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
