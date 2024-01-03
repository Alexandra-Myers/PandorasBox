/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToHeavenly extends PBEffectGenerate
{
    public PBEffectGenConvertToHeavenly() {}

    public PBEffectGenConvertToHeavenly(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range)
    {
        if (world instanceof ServerLevel serverLevel) {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0) {
                ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
                blocks.addAll(Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS);
                blocks.addAll(PandorasBox.flowers);
                ArrayListExtensions<Block> solid = new ArrayListExtensions<>();
                solid.addAll(Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.END_STONE, Blocks.NETHERRACK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.SAND, Blocks.RED_SAND, Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.MOSS_BLOCK, Blocks.DEEPSLATE, Blocks.TUFF);
                solid.addAll(PandorasBox.terracotta, PandorasBox.stained_terracotta);
                if (isBlockAnyOf(block, blocks)) {
                    setBlockToAirSafe(world, pos);
                } else if (isBlockAnyOf(block, solid)) {
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
                } else if (isBlockAnyOf(block, Blocks.OBSIDIAN, Blocks.LAVA, Blocks.ICE)) {
                    setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
                }

                if (isBlockAnyOf(block, Blocks.LAVA)) {
                    setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
                }

                if (isBlockAnyOf(block, Blocks.OBSIDIAN, Blocks.ICE)) {
                    setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
                }
            } else {
                Entity sheep = lazilySpawnEntity(world, entity, random, "sheep", 1.0f / (20 * 20), pos);
                canSpawnEntity(world, blockState, pos, sheep);
            }
            changeBiome(Biomes.LUSH_CAVES, pass, effectCenter, serverLevel);
        }
    }
}
