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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToDesert extends PBEffectGenerate {
    public PBEffectGenConvertToDesert() {}
    public PBEffectGenConvertToDesert(int time, double range, int unifiedSeed)
    {
        super(time, range, 1, unifiedSeed);
    }

    @Override
    public ResourceKey<Biome> getBiomeKey() {
        return Biomes.DESERT;
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range) {
        if (!world.isClientSide()) {
            Block block = world.getBlockState(pos).getBlock();

            if (pass == 0) {
                if (isBlockAnyOf(block, Either.right(BlockTags.LEAVES), Either.right(BlockTags.FLOWERS), Either.right(BlockTags.SNOW), Either.left(Blocks.ICE), Either.left(Blocks.WATER), Either.left(Blocks.VINE), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN), Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SEAGRASS), Either.left(Blocks.TALL_SEAGRASS), Either.left(Blocks.BROWN_MUSHROOM), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM), Either.left(Blocks.RED_MUSHROOM_BLOCK), Either.right(BlockTags.LOGS))) {
                    setBlockToAirSafe(world, pos);
                } else if (isBlockAnyOf(block, Either.right(BlockTags.SAND), Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(BlockTags.NYLIUM), Either.right(BlockTags.DIRT), Either.left(Blocks.NETHERRACK)) &&
                        !isBlockAnyOf(block, Either.left(Blocks.SAND))) {
                    setBlockSafe(world, pos, Blocks.SAND.defaultBlockState());

                    if (world.getBlockState(pos.above()).isAir()) {
                        if (random.nextInt(20 * 20) == 0) {
                            setBlockSafe(world, pos.above(1), Blocks.CACTUS.defaultBlockState());
                            setBlockSafe(world, pos.above(2), Blocks.CACTUS.defaultBlockState());
                            setBlockSafe(world, pos.above(3), Blocks.CACTUS.defaultBlockState());
                        } else if (random.nextInt(75) == 0) {
                            setBlockSafe(world, pos.above(1), Blocks.DEAD_BUSH.defaultBlockState());
                        }
                    }
                } else if (isBlockAnyOf(block, Either.right(PandorasBox.ALL_TERRACOTTA), Either.right(ConventionalBlockTags.STONES), Either.right(BlockTags.BASE_STONE_NETHER), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE))) {
                    setBlockSafe(world, pos, Blocks.SANDSTONE.defaultBlockState());
                }
            }
        }
    }
}
