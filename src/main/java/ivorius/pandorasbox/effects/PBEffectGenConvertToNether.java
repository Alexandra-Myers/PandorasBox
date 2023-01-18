/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

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
public class PBEffectGenConvertToNether extends PBEffectGenerate
{

    public PBEffectGenConvertToNether(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range)
    {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.STONE, Blocks.END_STONE, Blocks.MYCELIUM);
        for(Block block1 : ForgeRegistries.BLOCKS) {
            if(BlockTags.LOGS.contains(block1) || BlockTags.LEAVES.contains(block1) || BlockTags.SMALL_FLOWERS.contains(block1)) {
                blocks.add(block1);
            }
            if(block1.getRegistryName().getPath().endsWith("terracotta")) {
                misc.add(block1);
            }
        }

        if (pass == 0)
        {
            if (isBlockAnyOf(block, Blocks.COBBLESTONE, Blocks.ICE, Blocks.WATER, Blocks.OBSIDIAN))
            {
                setBlockSafe(world, pos, Blocks.LAVA.defaultBlockState());
            }
            else if (isBlockAnyOf(block, blocks))
            {
                setBlockToAirSafe(world, pos);
            }
            else if (isBlockAnyOf(block, Blocks.SAND))
            {
                setBlockSafe(world, pos, Blocks.SOUL_SAND.defaultBlockState());
            }
            else if (isBlockAnyOf(block, misc))
            {
                setBlockSafe(world, pos, Blocks.NETHERRACK.defaultBlockState());
            }
            else if (world.getBlockState(pos).isAir(world, pos))
            {
                if (world instanceof ServerWorld && random.nextInt(15) == 0)
                {
                    if (world.random.nextFloat() < 0.9f)
                    {
                        setBlockSafe(world, pos, Blocks.FIRE.defaultBlockState());
                    }
                    else
                    {
                        setBlockSafe(world, pos, Blocks.GLOWSTONE.defaultBlockState());
                    }
                }
            }
        }
        else
        {
            ArrayListExtensions<Entity> entities = new ArrayListExtensions<>();
            entities.addAll(
                    lazilySpawnEntity(world, entity, random, "sombie_pigman", 1.0f / (15 * 15), pos),
            lazilySpawnEntity(world, entity, random, "magma_cube", 1.0f / (15 * 15), pos));

            for(Entity entity1 : entities) {
                canSpawnEntity(world, blockState, pos, entity1);
            }

            if (canSpawnFlyingEntity(world, blockState, pos))
            {
                lazilySpawnFlyingEntity(world, entity, random, "ghast", 1.0f / (50 * 50 * 50), pos);
                lazilySpawnFlyingEntity(world, entity, random, "blaze", 1.0f / (50 * 50 * 50), pos);
            }
        }
    }
}
