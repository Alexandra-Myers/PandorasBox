/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.datafixers.util.Either;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.atlas.atlascore.util.ArrayListExtensions;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.Vec3;

import static ivorius.pandorasbox.effects.PBEffectGenConvertToNether.NetherBiome.expFromRatio;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToOverworld extends PBEffectGenerate {
    public PBEffectGenConvertToOverworld() {}

    public PBEffectGenConvertToOverworld(int time, double range, int unifiedSeed) {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public ResourceKey<Biome> getBiomeKey() {
        return Biomes.SUNFLOWER_PLAINS;
    }

    @Override
    public void generateOnBlock(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range) {
        if (!level.isClientSide()) {
            float newRatio = getRatioDone(entity.getTicksForEffect(this) + 1);
            BlockState blockState = level.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0) {
                if (isBlockAnyOf(block, Either.right(BlockTags.SNOW))) {
                    setBlockToAirSafe(level, pos);
                } else if (isBlockAnyOf(block, Either.right(PandorasBox.ALL_TERRACOTTA), Either.right(ConventionalBlockTags.STONES), Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(BlockTags.BASE_STONE_NETHER), Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(BlockTags.NYLIUM), Either.right(BlockTags.DIRT), Either.right(BlockTags.SAND), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE))) {
                    BlockPos posUp = pos.above();
                    if (level.getBlockState(posUp).getBlock() == Blocks.AIR) {
                        setBlockSafe(level, pos, Blocks.GRASS_BLOCK.defaultBlockState());
                    } else {
                        setBlockSafe(level, pos, Blocks.DIRT.defaultBlockState());
                    }
                } else if (isBlockAnyOf(block, Either.right(BlockTags.FIRE), Either.left(Blocks.BROWN_MUSHROOM), Either.left(Blocks.RED_MUSHROOM), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM_BLOCK))) {
                    setBlockSafe(level, pos, Blocks.AIR.defaultBlockState());
                }

                if (isBlockAnyOf(block, Either.left(Blocks.LAVA))) {
                    setBlockSafe(level, pos, Blocks.WATER.defaultBlockState());
                }
                if (isBlockAnyOf(block, Either.right(ConventionalBlockTags.OBSIDIANS), Either.left(Blocks.ICE))) {
                    setBlockSafe(level, pos, Blocks.WATER.defaultBlockState());
                }
            } else {
                ArrayListExtensions<Entity> entities = new ArrayListExtensions<>();
                entities.addAll(
                        lazilySpawnEntity(level, entity, random, "pig", 1.0f / (30 * 30), pos),
                        lazilySpawnEntity(level, entity, random, "sheep", 1.0f / (30 * 30), pos),
                        lazilySpawnEntity(level, entity, random, "cow", 1.0f / (30 * 30), pos),
                        lazilySpawnEntity(level, entity, random, "chicken", 1.0f / (30 * 30), pos));
                for (Entity entity1 : entities) {
                    canSpawnEntity(level, blockState, pos, entity1);
                }
            }
            if (level instanceof ServerLevel serverLevel && random.nextDouble() < Math.pow(0.05, expFromRatio(newRatio))) {
                BlockPos posBelow = pos.below();
                BlockState blockBelowState = level.getBlockState(posBelow);

                if (blockState.isAir() && blockBelowState.is(Blocks.GRASS_BLOCK) && blockBelowState.isRedstoneConductor(level, posBelow)) {
                    Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = level.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE);
                    int rand = random.nextInt(4);
                    switch (rand) {
                        case 0 -> configuredFeatureRegistry.getValueOrThrow(VegetationFeatures.PATCH_GRASS).place(serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos);
                        case 1 -> configuredFeatureRegistry.getValueOrThrow(VegetationFeatures.PATCH_SUNFLOWER).place(serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos);
                        case 2 -> configuredFeatureRegistry.getValueOrThrow(VegetationFeatures.TREES_PLAINS).place(serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos);
                        case 3 -> configuredFeatureRegistry.getValueOrThrow(VegetationFeatures.FLOWER_PLAIN).place(serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos);
                    }
                }
            }
        }
    }
}
