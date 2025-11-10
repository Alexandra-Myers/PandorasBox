package ivorius.pandorasbox.effectcreators.generate.feature_generators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.effects.generate.feature_generators.FeatureGenerator;
import ivorius.pandorasbox.init.Init;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface FeatureGeneratorCreator {
    Codec<FeatureGeneratorCreator> CODEC = Codec.withAlternative(Init.FEATURE_GENERATOR_CREATOR_TYPE_REGISTRY.byNameCodec()
            .dispatch(FeatureGeneratorCreator::codec, Function.identity()), FeatureGenerator.CODEC.xmap(DirectGeneratorCreator::new, DirectGeneratorCreator::featureGenerator));

    FeatureGenerator constructFeatureGenerator(Level world, double x, double y, double z, RandomSource random);

    @NotNull MapCodec<? extends FeatureGeneratorCreator> codec();
}
