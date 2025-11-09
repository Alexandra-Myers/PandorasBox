package ivorius.pandorasbox.effects.generate.feature_generators;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.FeatureInit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static ivorius.pandorasbox.effects.PBEffect.setBlockSafe;

public record GenerateHomo() implements FeatureGenerator {
    public static final MapCodec<GenerateHomo> CODEC = MapCodec.unit(GenerateHomo::new);
    @Override
    public void finalGenerate(ServerLevel serverLevel, BlockPos pos, BlockState blockState, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        if (random.nextInt(15 * 15) == 0) {
            BlockPos down = pos.below();
            BlockState state = serverLevel.getBlockState(down);
            boolean isSoil = state.getBlock() == Blocks.GRASS_BLOCK;
            if (!isSoil) return;

            Optional<Registry<ConfiguredFeature<?, ?>>> registry = serverLevel.registryAccess().lookup(Registries.CONFIGURED_FEATURE);
            ConfiguredFeature<?, ?> tree;

            if (registry.isEmpty()) return;
            tree = registry.get().getValueOrThrow(FeatureInit.RAINBOWS);
            tree.place(serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos);
        } else if (blockState.isAir() && Blocks.POPPY.defaultBlockState().canSurvive(serverLevel, pos)) {
            if (random.nextInt(3 * 3) == 0) {
                HolderSet.Named<Block> flowers = BuiltInRegistries.BLOCK.getOrThrow(BlockTags.SMALL_FLOWERS);
                int flowerIndex = random.nextInt(flowers.size());

                setBlockSafe(serverLevel, pos, flowers.get(flowerIndex).value().defaultBlockState());
            }
        }
    }

    @Override
    public double baseChance() {
        return 1;
    }

    @Override
    public @NotNull MapCodec<? extends FeatureGenerator> codec() {
        return CODEC;
    }
}
