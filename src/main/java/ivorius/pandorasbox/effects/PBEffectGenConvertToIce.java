/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
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
public class PBEffectGenConvertToIce extends PBEffectGenerate
{
    public PBEffectGenConvertToIce() {}

    public PBEffectGenConvertToIce(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range)
    {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        BlockState below = world.getBlockState(pos.below());

        if (pass == 0)
        {
            if (isBlockAnyOf(block, Blocks.WATER))
            {
                setBlockSafe(world, pos, Blocks.ICE.defaultBlockState());
            }
            else if (blockState.isAir(world, pos) && Blocks.SNOW.defaultBlockState().canSurvive(world, pos))
            {
                setBlockSafe(world, pos, Blocks.SNOW.defaultBlockState());
            }
            else if (isBlockAnyOf(block, Blocks.FIRE, Blocks.SOUL_FIRE))
            {
                setBlockSafe(world, pos, Blocks.AIR.defaultBlockState());
            }
            else if ((block == Blocks.LAVA && !blockState.getValue(FlowingFluidBlock.LEVEL).equals(0)) || block == Blocks.MAGMA_BLOCK)
            {
                setBlockSafe(world, pos, Blocks.ICE.defaultBlockState());
            }
            else if (block == Blocks.LAVA)
            {
                setBlockSafe(world, pos, Blocks.PACKED_ICE.defaultBlockState());
            }
            else if (world.loadedAndEntityCanStandOn(pos, entity))
            {
                int mode = random.nextInt(6);

                if (mode == 0)
                    setBlockSafe(world, pos, Blocks.ICE.defaultBlockState());
                else if (mode == 1)
                    setBlockSafe(world, pos, Blocks.PACKED_ICE.defaultBlockState());
                else if (mode == 2)
                    setBlockSafe(world, pos, Blocks.BLUE_ICE.defaultBlockState());
            }
        }
        else
        {
            Entity snowGolem = lazilySpawnEntity(world, entity, random, "snow_golem", 1.0f / (20 * 20), pos);
            canSpawnEntity(world, blockState, pos, snowGolem);
        }
    }
}
