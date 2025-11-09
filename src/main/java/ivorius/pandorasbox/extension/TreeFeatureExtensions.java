package ivorius.pandorasbox.extension;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

import java.util.Optional;

public interface TreeFeatureExtensions<FC extends FeatureConfiguration> {
    boolean pandorasBox$placeWithBlockOverrides(FeaturePlaceContext<FC> featurePlaceContext, BlockState trunk, BlockState leaves);

    default boolean pandorasBox$placeWithBlockOverrides(FC featureConfiguration, WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, RandomSource randomSource, BlockPos blockPos, BlockState trunk, BlockState leaves) {
        return worldGenLevel.ensureCanWrite(blockPos) && this.pandorasBox$placeWithBlockOverrides(new FeaturePlaceContext<>(Optional.empty(), worldGenLevel, chunkGenerator, randomSource, blockPos, featureConfiguration), trunk, leaves);
    }

    static boolean placeWithBlockOverrides(ConfiguredFeature<?, ?> configuredFeature, WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, RandomSource randomSource, BlockPos blockPos, BlockState trunk, BlockState leaves) {
        if (!(configuredFeature.feature() instanceof TreeFeatureExtensions treeFeatureExtensions)) return configuredFeature.place(worldGenLevel, chunkGenerator, randomSource, blockPos);
        return treeFeatureExtensions.pandorasBox$placeWithBlockOverrides(configuredFeature.config(), worldGenLevel, chunkGenerator, randomSource, blockPos, trunk, leaves);
    }
}
