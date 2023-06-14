/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.google.common.collect.Lists;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
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
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;

import java.util.Random;

import static ivorius.pandorasbox.effects.PBEffectGenConvertToNether.makeResolver;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToMushroom extends PBEffectGenerate
{
    public PBEffectGenConvertToMushroom() {}

    public PBEffectGenConvertToMushroom(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, int pass, BlockPos pos, double range)
    {
        if(world instanceof ServerLevel serverLevel){
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();
            ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
            blocks.addAll(PandorasBox.logs, PandorasBox.leaves, PandorasBox.flowers);

            blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS);

            if (pass == 0) {
                if (isBlockAnyOf(block, blocks)) {
                    setBlockToAirSafe(world, pos);
                } else if (isBlockAnyOf(block, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.TUFF, Blocks.DEEPSLATE, Blocks.END_STONE, Blocks.NETHERRACK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.SOUL_SOIL, Blocks.BASALT, Blocks.BLACKSTONE, Blocks.SOUL_SAND, Blocks.SAND, Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.MOSS_BLOCK)) {
                    BlockPos posUp = pos.above();

                    if (world.getBlockState(posUp).isAir()) {
                        setBlockSafe(world, pos, Blocks.MYCELIUM.defaultBlockState());

                        if (world.random.nextInt(6 * 6) == 0) {
                            setBlockSafe(world, posUp, (world.random.nextBoolean() ? Blocks.BROWN_MUSHROOM.defaultBlockState() : Blocks.RED_MUSHROOM.defaultBlockState()));
                        } else if (world.random.nextInt(8 * 8) == 0) {
                            boolean bl = random.nextBoolean();
                            Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = serverLevel.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                            ConfiguredFeature<?, ?> mushroomGen = bl ? configuredFeatureRegistry.get(TreeFeatures.HUGE_BROWN_MUSHROOM) : configuredFeatureRegistry.get(TreeFeatures.HUGE_RED_MUSHROOM);
                            assert mushroomGen != null;
                            mushroomGen.place(serverLevel, serverLevel.getChunkSource().getGenerator(), world.random, posUp);
                        }
                    } else {
                        setBlockSafe(world, pos, Blocks.DIRT.defaultBlockState());
                    }
                } else if (isBlockAnyOf(block, Blocks.OBSIDIAN, Blocks.LAVA, Blocks.ICE)) {
                    setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
                }

                if (isBlockAnyOf(block, Blocks.LAVA)) {
                    setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
                }
                if (isBlockAnyOf(block, Blocks.OBSIDIAN, Blocks.ICE)) {
                    setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
                }
            } else {
                Entity mooshroom = lazilySpawnEntity(world, entity, random, "mooshroom", 1.0f / (20 * 20), pos);
                canSpawnEntity(world, blockState, pos, mooshroom);
            }
            changeBiome(Biomes.MUSHROOM_FIELDS, pass, effectCenter, serverLevel);
        }
    }
}
