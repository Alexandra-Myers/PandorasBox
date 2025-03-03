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
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToHeavenly extends PBEffectGenerate {
    public PBEffectGenConvertToHeavenly() {}

    public PBEffectGenConvertToHeavenly(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public ResourceKey<Biome> getBiomeKey() {
        return Biomes.LUSH_CAVES;
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range) {
        if (!world.isClientSide()) {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0) {
                if (isBlockAnyOf(block, Either.right(BlockTags.SNOW), Either.right(BlockTags.FIRE), Either.right(BlockTags.FLOWERS), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN), Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SEAGRASS), Either.left(Blocks.TALL_SEAGRASS))) {
                    setBlockToAirSafe(world, pos);
                } else if (isBlockAnyOf(block, Either.right(PandorasBox.ALL_TERRACOTTA), Either.right(ConventionalBlockTags.STONES), Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(BlockTags.BASE_STONE_NETHER), Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(BlockTags.NYLIUM), Either.right(BlockTags.DIRT), Either.right(BlockTags.SAND), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE))) {
                    if (world.getBlockState(pos.above()).getBlock() == Blocks.AIR) {
                        if (world.random.nextInt(6 * 6) == 0) {
                            setBlockSafe(world, pos, Blocks.DIRT.defaultBlockState());
                            setBlockSafe(world, pos.above(), Blocks.OAK_LOG.defaultBlockState());
                            setBlockSafe(world, pos.above(2), Blocks.OAK_LEAVES.defaultBlockState());
                        } else if (world.random.nextInt(6 * 6) == 0) {
                            int pHeight = random.nextInt(5) + 3;
                            for (int yp = 0; yp < pHeight; yp++)
                                setBlockSafe(world, pos.above(yp), Blocks.QUARTZ_BLOCK.defaultBlockState());
                        } else if (world.random.nextInt(2 * 2) == 0) {
                            setBlockSafe(world, pos, Blocks.GLASS.defaultBlockState());
                        } else if (world.random.nextInt(8 * 8) == 0) {
                            setBlockSafe(world, pos, Blocks.GLASS.defaultBlockState());
                            setBlockSafe(world, pos.below(), Blocks.REDSTONE_LAMP.defaultBlockState());
                            setBlockSafe(world, pos.below(2), Blocks.REDSTONE_BLOCK.defaultBlockState());
                        } else
                            setBlockSafe(world, pos, Blocks.STONE_BRICKS.defaultBlockState());
                    } else {
                        setBlockSafe(world, pos, Blocks.STONE_BRICKS.defaultBlockState());
                    }
                } else if (isBlockAnyOf(block, Either.right(ConventionalBlockTags.OBSIDIANS), Either.left(Blocks.LAVA), Either.left(Blocks.ICE))) {
                    setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
                }
            } else {
                Entity sheep = lazilySpawnEntity(world, entity, random, "sheep", 1.0f / (20 * 20), pos);
                canSpawnEntity(world, blockState, pos, sheep);
            }
        }
    }
}
