/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToLifeless extends PBEffectGenerate
{

    public PBEffectGenConvertToLifeless(int time, double range, int unifiedSeed)
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
            ArrayListExtensions<Block> iCTWTGASTB = new ArrayListExtensions<>();
            ArrayListExtensions<Block> weird = new ArrayListExtensions<>();
            ArrayListExtensions<Block> hmm = new ArrayListExtensions<>();
            hmm.addAll(Blocks.NETHERRACK, Blocks.END_STONE);
            blocks.addAll(Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS);
            weird.addAll(Blocks.MYCELIUM, Blocks.GRASS, Blocks.CAKE);
            iCTWTGASTB.addAll(Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM, Blocks.FIRE);
            for(Block block1 : ForgeRegistries.BLOCKS) {
                if(BlockTags.LOGS.contains(block1) || BlockTags.LEAVES.contains(block1)) {
                    iCTWTGASTB.add(block1);
                }
                if(BlockTags.WOOL.contains(block1)) {
                    weird.add(block1);
                }
                if(BlockTags.SMALL_FLOWERS.contains(block1)) {
                    blocks.add(block1);
                }
                if(block1.getRegistryName().getPath().endsWith("_terracotta")) {
                    hmm.add(block1);
                }
            }
            if (isBlockAnyOf(block, Blocks.ICE, Blocks.WATER, Blocks.LAVA, Blocks.SNOW, Blocks.SNOW_BLOCK))
            {
                setBlockToAirSafe(world, pos);
            }
            else if (isBlockAnyOf(block, iCTWTGASTB))
            {
                setBlockToAirSafe(world, pos);
            }
            else if (isBlockAnyOf(block, blocks))
            {
                setBlockSafe(world, pos, Blocks.DEAD_BUSH.defaultBlockState());
            }
            else if (isBlockAnyOf(block, weird))
            {
                setBlockSafe(world, pos, Blocks.DIRT.defaultBlockState());
            }
            else if (isBlockAnyOf(block, hmm))
            {
                setBlockSafe(world, pos, Blocks.STONE.defaultBlockState());
            }
            else if (isBlockAnyOf(block, Blocks.SOUL_SAND))
            {
                setBlockSafe(world, pos, Blocks.SAND.defaultBlockState());
            }
        }
    }
}
