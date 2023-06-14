/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.worldgen.AccessibleTreeFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToHomo extends PBEffectGenerate
{
    public PBEffectGenConvertToHomo() {}

    public PBEffectGenConvertToHomo(int time, double range, int unifiedSeed)
    {
        super(time, range, 3, unifiedSeed);
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, int pass, BlockPos pos, double range)
    {
        if(world instanceof ServerLevel serverLevel) {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();
            ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
            blocks.addAll(PandorasBox.flowers);

            if (pass == 0) {
                if (isBlockAnyOf(block, Blocks.SNOW, Blocks.SNOW_BLOCK)) {
                    setBlockToAirSafe(world, pos);
                } else if (isBlockAnyOf(block, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.SANDSTONE, Blocks.END_STONE, Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.BASALT, Blocks.BLACKSTONE, Blocks.SAND, Blocks.MYCELIUM, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.DEEPSLATE, Blocks.TUFF)) {
                    if (world.getBlockState(pos.above()).isAir()) {
                        setBlockSafe(world, pos, Blocks.GRASS_BLOCK.defaultBlockState());
                    } else {
                        setBlockSafe(world, pos, Blocks.DIRT.defaultBlockState());
                    }
                } else if (isBlockAnyOf(block, Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK)) {
                    setBlockToAirSafe(world, pos);
                }

                if (isBlockAnyOf(block, Blocks.LAVA)) {
                    setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
                }
                if (isBlockAnyOf(block, Blocks.OBSIDIAN, Blocks.ICE)) {
                    setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
                }
            } else if (pass == 1) {
                if (random.nextInt(15 * 15) == 0) {
                    int[] lolliColors = new int[random.nextInt(4) + 1];
                    for (int i = 0; i < lolliColors.length; i++) {
                        lolliColors[i] = random.nextInt(16);
                    }

                    TreeFeature treeGen = (TreeFeature) PandorasBox.instance.RAINBOW;
                    if (treeGen instanceof AccessibleTreeFeature treeFeature) {
                        treeFeature.setMetas(lolliColors);
                        treeFeature.setSoil(Blocks.GRASS_BLOCK);
                        treeFeature.place(world, random, pos);
                    }
                } else if (blockState.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(world, pos)) {
                    if (random.nextInt(3 * 3) == 0) {
                        int meta;
                        meta = random.nextInt(10);

                        setBlockSafe(world, pos, blocks.get(meta).defaultBlockState());
                    }
                }
            } else {
                Sheep sheep = (Sheep) lazilySpawnEntity(world, entity, random, "sheep", 1.0f / (10 * 10), pos);
                if (canSpawnEntity(world, blockState, pos, sheep)) {
                    sheep.setColor(DyeColor.byId(random.nextInt(16)));
                }
            }
            changeBiome(Biomes.FLOWER_FOREST, pass, effectCenter, serverLevel);
        }
    }
}
