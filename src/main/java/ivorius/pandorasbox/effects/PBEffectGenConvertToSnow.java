/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.EntityPandorasBox;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToSnow extends PBEffectGenerate
{

    public PBEffectGenConvertToSnow(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public void generateOnBlock(World world, EntityPandorasBox entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range)
    {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        if (pass == 0)
        {
            if (isBlockAnyOf(block, Blocks.WATER))
            {
                setBlockSafe(world, pos, Blocks.ICE.defaultBlockState());
            }
            else if (blockState.isAir(world, pos))
            {
                setBlockSafe(world, pos, Blocks.SNOW.defaultBlockState());
            }
            else if (block == Blocks.FIRE)
            {
                setBlockSafe(world, pos, Blocks.AIR.defaultBlockState());
            }
            else if (block == Blocks.LAVA && !blockState.getValue(FlowingFluidBlock.LEVEL).equals(0))
            {
                setBlockSafe(world, pos, Blocks.COBBLESTONE.defaultBlockState());
            }
            else if (block == Blocks.LAVA)
            {
                setBlockSafe(world, pos, Blocks.OBSIDIAN.defaultBlockState());
            }
        }
        else
        {
            Entity entity1 = lazilySpawnEntity(world, entity, random, "snow_golem", 1.0f / (20 * 20), pos);
            canSpawnEntity(world, blockState, pos, entity1);
        }
    }
}
