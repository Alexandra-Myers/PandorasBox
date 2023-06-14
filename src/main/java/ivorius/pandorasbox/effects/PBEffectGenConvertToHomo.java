/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.worldgen.AccessibleTreeFeature;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

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
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range)
    {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        blocks.addAll(PandorasBox.flowers);

        if (pass == 0)
        {
            if (isBlockAnyOf(block, Blocks.SNOW, Blocks.SNOW_BLOCK))
            {
                setBlockToAirSafe(world, pos);
            }
            else if (isBlockAnyOf(block, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.END_STONE, Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.BASALT, Blocks.BLACKSTONE, Blocks.SAND, Blocks.RED_SAND, Blocks.MYCELIUM, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM))
            {
                if (world.getBlockState(pos.above()).isAir(world, pos.above()))
                {
                    setBlockSafe(world, pos, Blocks.GRASS_BLOCK.defaultBlockState());
                }
                else
                {
                    setBlockSafe(world, pos, Blocks.DIRT.defaultBlockState());
                }
            }
            else if (isBlockAnyOf(block, Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK))
            {
                setBlockToAirSafe(world, pos);
            }
            if (isBlockAnyOf(block, Blocks.OBSIDIAN, Blocks.ICE, Blocks.LAVA))
            {
                setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
            }
        }
        else if (pass == 1)
        {
            if (world instanceof ServerWorld)
            {
                if (random.nextInt(15 * 15) == 0)
                {
                    int[] lolliColors = new int[world.random.nextInt(4) + 1];
                    for (int i = 0; i < lolliColors.length; i++)
                    {
                        lolliColors[i] = world.random.nextInt(16);
                    }

                    AccessibleTreeFeature treeFeature = (AccessibleTreeFeature) PandorasBox.instance.RAINBOW;
                    treeFeature.setMetas(lolliColors);
                    treeFeature.setSoil(Blocks.GRASS_BLOCK);
                    treeFeature.place(world, random, pos);
                }
                else if (blockState.isAir(world, pos) && Blocks.SNOW.defaultBlockState().canSurvive(world, pos))
                {
                    if (random.nextInt(3 * 3) == 0)
                    {
                        int meta;
                        meta = random.nextInt(10);

                        setBlockSafe(world, pos, blocks.get(meta).defaultBlockState());
                    }
                }
            }
        }
        else
        {
            SheepEntity sheep = (SheepEntity) lazilySpawnEntity(world, entity, random, "sheep", 1.0f / (10 * 10), pos);
            if (canSpawnEntity(world, blockState, pos, sheep))
            {
                sheep.setColor(DyeColor.byId(random.nextInt(16)));
            }
        }
    }
}
