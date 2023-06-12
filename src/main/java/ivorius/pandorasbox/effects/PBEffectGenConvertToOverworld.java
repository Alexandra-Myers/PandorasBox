/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.google.common.collect.Lists;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.Random;

import static ivorius.pandorasbox.effects.PBEffectGenConvertToNether.makeResolver;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToOverworld extends PBEffectGenerate
{
    public PBEffectGenConvertToOverworld() {}

    public PBEffectGenConvertToOverworld(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, int pass, BlockPos pos, double range)
    {
        if(world instanceof ServerLevel serverLevel){
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            ArrayListExtensions<Block> grass = new ArrayListExtensions<>();
            grass.addAll(Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS);

            if (pass == 0) {
                if (isBlockAnyOf(block, Blocks.SNOW, Blocks.SNOW_BLOCK)) {
                    setBlockToAirSafe(world, pos);
                } else if (isBlockAnyOf(block, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.SANDSTONE, Blocks.END_STONE, Blocks.NETHERRACK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.SOUL_SOIL, Blocks.BASALT, Blocks.BLACKSTONE, Blocks.SOUL_SAND, Blocks.SAND, Blocks.MYCELIUM, Blocks.DIRT)) {
                    BlockPos posUp = pos.above();
                    if (world.getBlockState(posUp).getBlock() == Blocks.AIR) {
                        setBlockSafe(world, pos, Blocks.GRASS_BLOCK.defaultBlockState());

                        if (random.nextInt(2 * 2) == 0) {
                            setBlockSafe(world, posUp, grass.get(random.nextInt(4)).defaultBlockState());
                        } else if (random.nextInt(5 * 5) == 0) {
                            setBlockSafe(world, posUp, (world.random.nextBoolean() ? Blocks.POPPY.defaultBlockState() : Blocks.DANDELION.defaultBlockState()));
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
                for (Entity entity1 : entities) {
                    canSpawnEntity(world, blockState, pos, entity1);
                }
            }
            changeBiome(Biomes.SUNFLOWER_PLAINS, pass, effectCenter, serverLevel);
        }
    }
}
