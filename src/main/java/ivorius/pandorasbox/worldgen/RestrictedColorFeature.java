package ivorius.pandorasbox.worldgen;

import ivorius.pandorasbox.random.IValue;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface RestrictedColorFeature<FC extends FeatureConfiguration> {
    boolean pandorasBox$placeWithRestrictedColors(FeaturePlaceContext<@NotNull FC> featurePlaceContext, IValue possibleColors, BlockState soil);

    default boolean pandorasBox$placeWithRestrictedColors(FC featureConfiguration, WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, RandomSource randomSource, BlockPos blockPos, IValue possibleColors, BlockState soil) {
        return worldGenLevel.ensureCanWrite(blockPos) && this.pandorasBox$placeWithRestrictedColors(new FeaturePlaceContext<>(Optional.empty(), worldGenLevel, chunkGenerator, randomSource, blockPos, featureConfiguration), possibleColors, soil);
    }

    static boolean placeWithRestrictedColors(ConfiguredFeature<?, ?> configuredFeature, WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, RandomSource randomSource, BlockPos blockPos, IValue possibleColors, BlockState soil) {
        if (!(configuredFeature.feature() instanceof RestrictedColorFeature restrictedColorFeature)) return configuredFeature.place(worldGenLevel, chunkGenerator, randomSource, blockPos);
        return restrictedColorFeature.pandorasBox$placeWithRestrictedColors(configuredFeature.config(), worldGenLevel, chunkGenerator, randomSource, blockPos, possibleColors, soil);
    }
}
