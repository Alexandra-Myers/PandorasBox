/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.EntityPandorasBox;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToEnd extends PBEffectGenerate
{

    public PBEffectGenConvertToEnd(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public void generateOnBlock(World world, EntityPandorasBox entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range)
    {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        blocks.addAll(Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK);
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        misc.addAll(Blocks.ICE, Blocks.WATER, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.VINE, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM);
        for(Block block1 : ForgeRegistries.BLOCKS) {
            if(BlockTags.LOGS.contains(block1)) {
                blocks.add(block1);
            }
            if(BlockTags.LEAVES.contains(block1) || BlockTags.SMALL_FLOWERS.contains(block1)) {
                misc.add(block1);
            }
        }

        if (pass == 0)
        {
            if (isBlockAnyOf(block, Blocks.OBSIDIAN))
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
    }
}
