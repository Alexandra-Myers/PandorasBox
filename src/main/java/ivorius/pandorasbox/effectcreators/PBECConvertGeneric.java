package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenerate;
import ivorius.pandorasbox.effects.generate.SimpleConvertEffect;
import ivorius.pandorasbox.random.DValue;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record PBECConvertGeneric(DValue range, float chanceForMoreEffects, SimpleConvertEffect simpleConvertEffect) implements PBEffectCreator {
    public static final MapCodec<PBECConvertGeneric> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(DValue.CODEC.fieldOf("range").forGetter(PBECConvertGeneric::range),
                            Codec.floatRange(0, 1).fieldOf("chance_for_more_effects").forGetter(PBECConvertGeneric::chanceForMoreEffects),
                            SimpleConvertEffect.CODEC.codec().fieldOf("convert_effect").forGetter(PBECConvertGeneric::simpleConvertEffect))
                    .apply(instance, PBECConvertGeneric::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);
        int passes = 1;
        if (!simpleConvertEffect.spawners().isEmpty()) passes = 3;
        else if (!simpleConvertEffect.generators().isEmpty()) passes = 2;

        return new PBEffectGenerate(time, range, passes, PandorasBoxHelper.getRandomUnifiedSeed(random), simpleConvertEffect);
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
