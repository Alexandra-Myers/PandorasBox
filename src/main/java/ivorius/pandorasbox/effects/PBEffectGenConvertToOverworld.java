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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToOverworld extends PBEffectGenerate {
    public PBEffectGenConvertToOverworld() {}

    public PBEffectGenConvertToOverworld(int time, double range, int unifiedSeed) {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();

        ArrayListExtensions<Block> grass = new ArrayListExtensions<>();
        grass.addAll(Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS);
        ArrayListExtensions<Block> solid = new ArrayListExtensions<>();
        solid.addAll(Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.END_STONE, Blocks.NETHERRACK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.SOUL_SOIL, Blocks.BASALT, Blocks.BLACKSTONE, Blocks.SOUL_SAND, Blocks.SAND, Blocks.RED_SAND, Blocks.MYCELIUM, Blocks.DIRT);
        solid.addAll(PandorasBox.terracotta, PandorasBox.stained_terracotta);

        if (pass == 0) {
            if (isBlockAnyOf(block, Blocks.SNOW, Blocks.SNOW_BLOCK)) {
                setBlockToAirSafe(world, pos);
            } else if (isBlockAnyOf(block, solid)) {
                BlockPos posUp = pos.above();
                if (world.getBlockState(posUp).getBlock() == Blocks.AIR) {
                    setBlockSafe(world, pos, Blocks.GRASS_BLOCK.defaultBlockState());

                    if (!world.isClientSide) {
                        if (random.nextInt(2 * 2) == 0) {
                            setBlockSafe(world, posUp, grass.get(random.nextInt(4)).defaultBlockState());
                        } else if (random.nextInt(5 * 5) == 0) {
                            setBlockSafe(world, posUp, (world.random.nextBoolean() ? Blocks.POPPY.defaultBlockState() : Blocks.DANDELION.defaultBlockState()));
                        }
                    }
                } else {
                    setBlockSafe(world, pos, Blocks.DIRT.defaultBlockState());
                }
            } else if (isBlockAnyOf(block, Blocks.FIRE, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK)) {
                setBlockSafe(world, pos, Blocks.AIR.defaultBlockState());
            }

            if (isBlockAnyOf(block, Blocks.LAVA)) {
                setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
            }
            if (isBlockAnyOf(block, Blocks.OBSIDIAN, Blocks.ICE)) {
                setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
            }
        } else {
            ArrayListExtensions<Entity> entities = new ArrayListExtensions<>();
            entities.addAll(
                    lazilySpawnEntity(world, entity, random, "pig", 1.0f / (30 * 30), pos),
            lazilySpawnEntity(world, entity, random, "sheep", 1.0f / (30 * 30), pos),
            lazilySpawnEntity(world, entity, random, "cow", 1.0f / (30 * 30), pos),
            lazilySpawnEntity(world, entity, random, "chicken", 1.0f / (30 * 30), pos));
            for(Entity entity1 : entities) {
                canSpawnEntity(world, blockState, pos, entity1);
            }
        }
    }
}
