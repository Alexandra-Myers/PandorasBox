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
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToMushroom extends PBEffectGenerate
{

    public PBEffectGenConvertToMushroom(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range)
    {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        for(Block block1 : ForgeRegistries.BLOCKS) {
            if(BlockTags.LOGS.contains(block1) || BlockTags.LEAVES.contains(block1) || BlockTags.SMALL_FLOWERS.contains(block1)) {
                blocks.add(block1);
            }
        }

        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.FIRE, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS);

        if (pass == 0)
        {
            if (isBlockAnyOf(block, blocks))
            {
                setBlockToAirSafe(world, pos);
            }
            else if (isBlockAnyOf(block, Blocks.STONE, Blocks.END_STONE, Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.SAND, Blocks.DIRT, Blocks.GRASS_BLOCK))
            {
                BlockPos posUp = pos.above();

                if (world.getBlockState(posUp).isAir(world, posUp))
                {
                    setBlockSafe(world, pos, Blocks.MYCELIUM.defaultBlockState());

                    if (world instanceof ServerWorld)
                    {
                        if (world.random.nextInt(6 * 6) == 0)
                        {
                            setBlockSafe(world, posUp, (world.random.nextBoolean() ? Blocks.BROWN_MUSHROOM.defaultBlockState() : Blocks.RED_MUSHROOM.defaultBlockState()));
                        }
                        else if (world.random.nextInt(8 * 8) == 0)
                        {
                            boolean bl = random.nextBoolean();
                            ConfiguredFeature<?, ?> mushroomGen = bl ? Features.HUGE_BROWN_MUSHROOM : Features.HUGE_RED_MUSHROOM;
                            ServerWorld serverWorld = (ServerWorld) world;
                            mushroomGen.place(serverWorld, serverWorld.getChunkSource().getGenerator(), world.random, posUp);
                        }
                    }
                }
                else
                {
                    setBlockSafe(world, pos, Blocks.DIRT.defaultBlockState());
                }
            }
            else if (isBlockAnyOf(block, Blocks.OBSIDIAN, Blocks.LAVA, Blocks.ICE))
            {
                setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
            }

            if (isBlockAnyOf(block, Blocks.LAVA))
            {
                setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
            }
            if (isBlockAnyOf(block, Blocks.OBSIDIAN, Blocks.ICE))
            {
                setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
            }
        }
        else
        {
            Entity mooshroom = lazilySpawnEntity(world, entity, random, "mooshroom", 1.0f / (20 * 20), pos);
            canSpawnEntity(world, blockState, pos, mooshroom);
        }
    }
}
