/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.datafixers.util.Either;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
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

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToMushroom extends PBEffectGenerate {
    public PBEffectGenConvertToMushroom() {}

    public PBEffectGenConvertToMushroom(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public ResourceKey<Biome> getBiomeKey() {
        return Biomes.MUSHROOM_FIELDS;
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range) {
        if (world instanceof ServerLevel serverLevel) {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0) {
                if (isBlockAnyOf(block, Either.right(BlockTags.LOGS), Either.right(BlockTags.LEAVES), Either.right(BlockTags.FLOWERS),
                        Either.right(BlockTags.SNOW), Either.right(BlockTags.FIRE), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN),
                        Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SEAGRASS), Either.left(Blocks.TALL_SEAGRASS))) {
                    setBlockToAirSafe(world, pos);
                } else if (isBlockAnyOf(block, Either.right(PandorasBox.ALL_TERRACOTTA), Either.right(ConventionalBlockTags.STONES), Either.right(ConventionalBlockTags.COBBLESTONES),
                        Either.right(BlockTags.BASE_STONE_NETHER), Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(BlockTags.NYLIUM),
                        Either.right(BlockTags.DIRT), Either.right(BlockTags.SAND), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS),
                        Either.left(Blocks.END_STONE))) {
                    BlockPos posUp = pos.above();

                    if (world.getBlockState(posUp).isAir()) {
                        setBlockSafe(world, pos, Blocks.MYCELIUM.defaultBlockState());

                        if (world.random.nextInt(6 * 6) == 0) {
                            setBlockSafe(world, posUp, (world.random.nextBoolean() ? Blocks.BROWN_MUSHROOM.defaultBlockState() : Blocks.RED_MUSHROOM.defaultBlockState()));
                        } else if (world.random.nextInt(8 * 8) == 0) {
                            boolean bl = random.nextBoolean();
                            Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = serverLevel.registryAccess().lookupOrThrow(Registries.CONFIGURED_FEATURE);
                            ConfiguredFeature<?, ?> mushroomGen = bl ? configuredFeatureRegistry.getValueOrThrow(TreeFeatures.HUGE_BROWN_MUSHROOM) : configuredFeatureRegistry.getValueOrThrow(TreeFeatures.HUGE_RED_MUSHROOM);
                            mushroomGen.place(serverLevel, serverLevel.getChunkSource().getGenerator(), world.random, posUp);
                        }
                    } else {
                        setBlockSafe(world, pos, Blocks.DIRT.defaultBlockState());
                    }
                } else if (isBlockAnyOf(block, Either.right(ConventionalBlockTags.OBSIDIANS), Either.left(Blocks.LAVA), Either.left(Blocks.ICE))) {
                    setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
                }
            } else {
                Entity mooshroom = lazilySpawnEntity(world, entity, random, "mooshroom", 1.0f / (20 * 20), pos);
                canSpawnEntity(world, blockState, pos, mooshroom);
            }
        }
    }
}
