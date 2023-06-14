/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

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
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range)
    {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        if (pass == 0)
        {
            ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
            blocks.addAll(Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS);
            blocks.addAll(PandorasBox.flowers);
            if (isBlockAnyOf(block, blocks))
            {
                setBlockToAirSafe(world, pos);
            }
            else if (isBlockAnyOf(block, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.END_STONE, Blocks.NETHERRACK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.SAND, Blocks.RED_SAND, Blocks.DIRT, Blocks.GRASS_BLOCK))
            {
                if (world.getBlockState(pos.above()).getBlock() == Blocks.AIR)
                {
                    if (world instanceof ServerWorld && world.random.nextInt(6 * 6) == 0)
                    {
                        setBlockSafe(world, pos, Blocks.DIRT.defaultBlockState());
                        setBlockSafe(world, pos.above(), Blocks.OAK_LOG.defaultBlockState());
                        setBlockSafe(world, pos.above(2), Blocks.OAK_LEAVES.defaultBlockState());
                    }
                    else if (world instanceof ServerWorld && world.random.nextInt(6 * 6) == 0)
                    {
                        int pHeight = random.nextInt(5) + 3;
                        for (int yp = 0; yp < pHeight; yp++)
                            setBlockSafe(world, pos.above(yp), Blocks.QUARTZ_BLOCK.defaultBlockState());
                    }
                    else if (world instanceof ServerWorld && world.random.nextInt(2 * 2) == 0)
                    {
                        setBlockSafe(world, pos, Blocks.GLASS.defaultBlockState());
                    }
                    else if (world instanceof ServerWorld && world.random.nextInt(8 * 8) == 0)
                    {
                        setBlockSafe(world, pos, Blocks.GLASS.defaultBlockState());
                        setBlockSafe(world, pos.below(), Blocks.REDSTONE_LAMP.defaultBlockState());
                        setBlockSafe(world, pos.below(2), Blocks.REDSTONE_BLOCK.defaultBlockState());
                    }
                    else
                        setBlockSafe(world, pos, Blocks.STONE_BRICKS.defaultBlockState());
                }
                else
                {
                    setBlockSafe(world, pos, Blocks.STONE_BRICKS.defaultBlockState());
                }
            }
            else if (isBlockAnyOf(block, Blocks.OBSIDIAN, Blocks.LAVA, Blocks.ICE))
            {
                setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
            }
        }
        else
        {
            Entity sheep = lazilySpawnEntity(world, entity, random, "sheep", 1.0f / (20 * 20), pos);
            canSpawnEntity(world, blockState, pos, sheep);
        }
    }
}
