/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToDesert extends PBEffectGenerate
{
    public PBEffectGenConvertToDesert() {}
    public PBEffectGenConvertToDesert(int time, double range, int unifiedSeed)
    {
        super(time, range, 1, unifiedSeed);
    }

    @Override
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range)
    {
        Block block = world.getBlockState(pos).getBlock();

        if (pass == 0)
        {
            ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
            blocks.addAll(Blocks.ICE, Blocks.WATER, Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.BROWN_MUSHROOM, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM, Blocks.RED_MUSHROOM_BLOCK);
            ArrayListExtensions<Block> soil = new ArrayListExtensions<>();
            ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
            soil.addAll(Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.RED_SAND, Blocks.GRASS_BLOCK, Blocks.NETHERRACK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.DIRT, Blocks.MYCELIUM);
            misc.addAll(Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.RED_SANDSTONE, Blocks.BLACKSTONE, Blocks.BASALT, Blocks.END_STONE);
            blocks.addAll(PandorasBox.flowers, PandorasBox.leaves, PandorasBox.logs);
            misc.addAll(PandorasBox.terracotta);
            if (isBlockAnyOf(block, blocks))
            {
                setBlockToAirSafe(world, pos);
            }
            else if (isBlockAnyOf(block, soil))
            {
                setBlockSafe(world, pos, Blocks.SAND.defaultBlockState());

                if (world.getBlockState(pos.above()).isAir(world, pos.above()))
                {
                    if (world instanceof ServerWorld && random.nextInt(20 * 20) == 0)
                    {
                        setBlockSafe(world, pos.above(1), Blocks.CACTUS.defaultBlockState());
                        setBlockSafe(world, pos.above(2), Blocks.CACTUS.defaultBlockState());
                        setBlockSafe(world, pos.above(3), Blocks.CACTUS.defaultBlockState());
                    }
                }
            } else if (isBlockAnyOf(block, misc)){
                setBlockSafe(world, pos, Blocks.SANDSTONE.defaultBlockState());
            }
        }
    }
}
