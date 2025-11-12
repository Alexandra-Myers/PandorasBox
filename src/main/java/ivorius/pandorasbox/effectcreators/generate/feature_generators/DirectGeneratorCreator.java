package ivorius.pandorasbox.effectcreators.generate.feature_generators;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.effects.generate.feature_generators.FeatureGenerator;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record DirectGeneratorCreator(FeatureGenerator featureGenerator) implements FeatureGeneratorCreator {
    public static final MapCodec<DirectGeneratorCreator> CODEC = FeatureGenerator.MAP_CODEC.xmap(DirectGeneratorCreator::new, DirectGeneratorCreator::featureGenerator);

    @Override
    public FeatureGenerator constructFeatureGenerator(Level world, double x, double y, double z, RandomSource random) {
        return featureGenerator;
    }

    @Override
    public @NotNull MapCodec<? extends FeatureGeneratorCreator> codec() {
        return CODEC;
    }
}
