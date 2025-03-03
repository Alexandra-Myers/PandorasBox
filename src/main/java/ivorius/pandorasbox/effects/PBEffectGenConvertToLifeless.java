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
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.level.block.Block.dropResources;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToLifeless extends PBEffectGenerate {
    public PBEffectGenConvertToLifeless() {}

    public PBEffectGenConvertToLifeless(int time, double range, int unifiedSeed)
    {
        super(time, range, 1, unifiedSeed);
    }

    @Override
    public ResourceKey<Biome> getBiomeKey() {
        return Biomes.BADLANDS;
    }

    @Override
    public void generateOnBlock(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range) {
        if (!level.isClientSide()) {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            if (pass == 0) {
                FluidState fluidstate = level.getFluidState(pos);
                if (fluidstate.is(FluidTags.WATER)) {
                    if (block instanceof BucketPickup bucketPickup && !bucketPickup.pickupBlock(null, level, pos, state).isEmpty()) return;

                    if (!(state.getBlock() instanceof LiquidBlock)) {
                        if (!state.is(Blocks.KELP) && !state.is(Blocks.KELP_PLANT) && !state.is(Blocks.SEAGRASS) && !state.is(Blocks.TALL_SEAGRASS)) return;

                        BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
                        dropResources(state, level, pos, blockEntity);
                    }
                    setBlockToAirSafe(level, pos);
                    for (Direction direction : Direction.values()) {
                        BlockPos pos1 = pos.relative(direction);
                        BlockState state1 = level.getBlockState(pos1);
                        Block block1 = state1.getBlock();
                        FluidState fluidstate1 = level.getFluidState(pos1);
                        if (fluidstate1.is(FluidTags.WATER)) {
                            if (block1 instanceof BucketPickup bucketPickup && !bucketPickup.pickupBlock(null, level, pos1, state1).isEmpty())
                                return;

                            if (!(state1.getBlock() instanceof LiquidBlock)) {
                                if (!state1.is(Blocks.KELP) && !state1.is(Blocks.KELP_PLANT) && !state1.is(Blocks.SEAGRASS) && !state1.is(Blocks.TALL_SEAGRASS))
                                    return;

                                BlockEntity blockEntity = state1.hasBlockEntity() ? level.getBlockEntity(pos1) : null;
                                dropResources(state1, level, pos1, blockEntity);
                            }
                            setBlockToAirSafe(level, pos1);
                        }
                    }
                } else if (isBlockAnyOf(block, Either.left(Blocks.ICE), Either.left(Blocks.WATER), Either.left(Blocks.LAVA), Either.right(BlockTags.SNOW))) {
                    setBlockToAirSafe(level, pos);
                } else if (isBlockAnyOf(block, Either.right(BlockTags.LOGS), Either.right(BlockTags.LEAVES), Either.left(Blocks.VINE), Either.left(Blocks.BROWN_MUSHROOM), Either.left(Blocks.RED_MUSHROOM), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM_BLOCK))) {
                    setBlockToAirSafe(level, pos);
                } else if (isBlockAnyOf(block, Either.right(BlockTags.FLOWERS), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN), Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SEAGRASS), Either.left(Blocks.TALL_SEAGRASS))) {
                    setBlockSafe(level, pos, Blocks.DEAD_BUSH.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(BlockTags.NYLIUM), Either.right(BlockTags.DIRT), Either.right(BlockTags.WOOL), Either.left(Blocks.CAKE))) {
                    setBlockSafe(level, pos, Blocks.DIRT.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(PandorasBox.ALL_TERRACOTTA), Either.right(ConventionalBlockTags.STONES), Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(BlockTags.BASE_STONE_NETHER), Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE))) {
                    setBlockSafe(level, pos, Blocks.STONE.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.left(Blocks.RED_SAND))) {
                    setBlockSafe(level, pos, Blocks.SAND.defaultBlockState());
                }
            }
        }
    }
}
