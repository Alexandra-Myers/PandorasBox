package ivorius.pandorasbox.extension;

import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.worldgen.RestrictedColorFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface TreeFeatureExtensions<FC extends FeatureConfiguration> extends RestrictedColorFeature<FC> {
    boolean pandorasBox$placeWithBlockOverrides(FeaturePlaceContext<@NotNull FC> featurePlaceContext, BlockState trunk, BlockState leaves, @Nullable BlockState soil);

    default boolean pandorasBox$placeWithBlockOverrides(FC featureConfiguration, WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, RandomSource randomSource, BlockPos blockPos, BlockState trunk, BlockState leaves, @Nullable BlockState soil) {
        return worldGenLevel.ensureCanWrite(blockPos) && this.pandorasBox$placeWithBlockOverrides(new FeaturePlaceContext<>(Optional.empty(), worldGenLevel, chunkGenerator, randomSource, blockPos, featureConfiguration), trunk, leaves, soil);
    }

    static boolean placeWithBlockOverrides(ConfiguredFeature<?, ?> configuredFeature, WorldGenLevel worldGenLevel, ChunkGenerator chunkGenerator, RandomSource randomSource, BlockPos blockPos, BlockState trunk, BlockState leaves, @Nullable BlockState soil) {
        if (!(configuredFeature.feature() instanceof TreeFeatureExtensions treeFeatureExtensions)) return configuredFeature.place(worldGenLevel, chunkGenerator, randomSource, blockPos);
        return treeFeatureExtensions.pandorasBox$placeWithBlockOverrides(configuredFeature.config(), worldGenLevel, chunkGenerator, randomSource, blockPos, trunk, leaves, soil);
    }

    @Override
    default boolean pandorasBox$placeWithRestrictedColors(FeaturePlaceContext<@NotNull FC> featurePlaceContext, IValue possibleColors, BlockState soil) {
        HolderSet.Named<Block> blocks = BuiltInRegistries.BLOCK.getTag(BlockTags.WOOL).orElseThrow();
        if (blocks.size() == 0) return false;
        int[] indexes = new int[possibleColors.getValue(featurePlaceContext.random())];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = featurePlaceContext.random().nextInt(blocks.size());
        }
        Block wool = blocks.get(indexes[featurePlaceContext.random().nextInt(indexes.length)]).value();
        return pandorasBox$placeWithBlockOverrides(featurePlaceContext, wool.defaultBlockState(), wool.defaultBlockState(), soil);
    }
}
