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
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToEnd extends PBEffectGenerate
{
    private int timesFeatureAMade = 0;
    public PBEffectGenConvertToEnd() {}

    public PBEffectGenConvertToEnd(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range)
    {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        blocks.addAll(Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK);
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        misc.addAll(Blocks.ICE, Blocks.WATER, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.VINE, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM);
        blocks.addAll(PandorasBox.logs);
        misc.addAll(PandorasBox.leaves, PandorasBox.flowers);

        if (pass == 0)
        {
            if (isBlockAnyOf(block, Blocks.OBSIDIAN, Blocks.CHORUS_PLANT, Blocks.CHORUS_FLOWER))
            {

            }
            else if (isBlockAnyOf(block, blocks))
            {
                setBlockSafe(world, pos, Blocks.OBSIDIAN.defaultBlockState());
            }
            else if (isBlockAnyOf(block, misc))
            {
                setBlockToAirSafe(world, pos);
            }
            else if (world.loadedAndEntityCanStandOn(pos, entity))
            {
                setBlockSafe(world, pos, Blocks.END_STONE.defaultBlockState());
            }
        }
        else
        {
            Entity enderman = lazilySpawnEntity(world, entity, random, "enderman", 1.0f / (20 * 20), pos);
            canSpawnEntity(world, blockState, pos, enderman);
        }
        if (random.nextDouble() < Math.pow(0.2, Math.floor(timesFeatureAMade / 16.0)))
        {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.getMaterial() == Material.AIR && !isBlockAnyOf(blockBelowState.getBlock(), Blocks.CHORUS_FLOWER, Blocks.CHORUS_PLANT, Blocks.OBSIDIAN) && blockBelowState.isRedstoneConductor(world, posBelow) && world instanceof ServerWorld)
            {
                setBlockSafe(world, posBelow, Blocks.END_STONE.defaultBlockState());
                ServerWorld serverWorld = (ServerWorld) world;
                boolean success = Features.CHORUS_PLANT.place(serverWorld, serverWorld.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureAMade++;
            }
        }
    }
}
