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
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

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
            ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
            misc.addAll(Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.BLACKSTONE, Blocks.BASALT, Blocks.END_STONE, Blocks.NETHERRACK, Blocks.GRASS_BLOCK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.DIRT, Blocks.MYCELIUM);
            for(Block block1 : ForgeRegistries.BLOCKS) {
                if(BlockTags.LOGS.contains(block1) || BlockTags.LEAVES.contains(block1) || BlockTags.SMALL_FLOWERS.contains(block1)) {
                    blocks.add(block1);
                }
                if(block1.getRegistryName().getPath().endsWith("terracotta")) {
                    misc.add(block1);
                }
            }
            if (isBlockAnyOf(block, blocks))
            {
                setBlockToAirSafe(world, pos);
            }
            else if (isBlockAnyOf(block, misc))
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
            }
        }
    }
}
