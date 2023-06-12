/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.Optional;
import java.util.Random;

import static net.minecraft.data.worldgen.features.NetherFeatures.*;
import static net.minecraft.data.worldgen.features.TreeFeatures.CRIMSON_FUNGUS_PLANTED;
import static net.minecraft.data.worldgen.features.TreeFeatures.WARPED_FUNGUS_PLANTED;
import static net.minecraft.data.worldgen.placement.NetherPlacements.WARPED_FOREST_VEGETATION;
import static net.minecraft.data.worldgen.placement.VegetationPlacements.BROWN_MUSHROOM_NETHER;
import static net.minecraft.data.worldgen.placement.VegetationPlacements.RED_MUSHROOM_NETHER;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToNether extends PBEffectGenerate
{
    private String biome;
    private int timesFeatureAMade = 0;
    private int timesFeatureBMade = 0;
    public PBEffectGenConvertToNether() {}

    public PBEffectGenConvertToNether(int time, double range, int unifiedSeed, String biome)
    {
        super(time, range, 2, unifiedSeed);
        this.biome = biome;
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, int pass, BlockPos pos, double range)
    {
        switch (biome) {
            case "wastes" -> createWastes(world, entity, random, pass, pos);
            case "soul_sand_valley" -> createSoul(world, entity, random, pass, pos);
            case "crimson" -> createCrimson(world, entity, random, pass, pos);
            case "warped" -> createWarped(world, entity, random, pass, pos);
            case "deltas" -> createDeltas(world, entity, random, pass, pos);
        }
    }
    public void createWastes(Level world, PandorasBoxEntity entity, RandomSource random, int pass, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.END_STONE, Blocks.MYCELIUM);
        blocks.addAll(PandorasBox.flowers, PandorasBox.logs, PandorasBox.leaves);
        misc.addAll(PandorasBox.terracotta);

        if (pass == 0) {
            if (isBlockAnyOf(block, Blocks.COBBLESTONE, Blocks.ICE, Blocks.WATER, Blocks.OBSIDIAN)) {
                Optional<Integer> integer = blockState.getOptionalValue(LiquidBlock.LEVEL);
                BlockState blockState2 = Blocks.LAVA.defaultBlockState();
                if(integer.isPresent()) {
                    blockState2 = blockState2.setValue(LiquidBlock.LEVEL, integer.get());
                }
                setBlockSafe(world, pos, blockState2);
            } else if (isBlockAnyOf(block, blocks)) {
                setBlockToAirSafe(world, pos);
            } else if (isBlockAnyOf(block, Blocks.CLAY)) {
                setBlockSafe(world, pos, Blocks.MAGMA_BLOCK.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.COAL_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GOLD_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_GOLD_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.SAND)) {
                setBlockSafe(world, pos, Blocks.SOUL_SAND.defaultBlockState());
            } else if (isBlockAnyOf(block, misc)) {
                setBlockSafe(world, pos, Blocks.NETHERRACK.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GRANITE)) {
                setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
            } else if (world.getBlockState(pos).isAir()) {
                if (world instanceof ServerLevel && random.nextInt(25) == 0) {
                    if (world.random.nextFloat() < 0.9f) {
                        setBlockSafe(world, pos, Blocks.FIRE.defaultBlockState());
                    } else {
                        setBlockSafe(world, pos, Blocks.GLOWSTONE.defaultBlockState());
                    }
                }
            }
        } else {
            ArrayListExtensions<Entity> entities = new ArrayListExtensions<>();
            entities.addAll(
                    lazilySpawnEntity(world, entity, random, "piglin", 1.0f / (30 * 30), pos),
                    lazilySpawnEntity(world, entity, random, "zombified_piglin", 1.0f / (15 * 15), pos),
                    lazilySpawnEntity(world, entity, random, "magma_cube", 1.0f / (15 * 15), pos),
                    lazilySpawnEntity(world, entity, random, "hoglin", 1.0f / (20 * 20), pos));

            for (Entity entity1 : entities) {
                canSpawnEntity(world, blockState, pos, entity1);
            }

            if (canSpawnFlyingEntity(world, blockState, pos)) {
                lazilySpawnFlyingEntity(world, entity, random, "ghast", 1.0f / (50 * 50 * 50), pos);
                lazilySpawnFlyingEntity(world, entity, random, "blaze", 1.0f / (50 * 50 * 50), pos);
            }
        }
        if (random.nextDouble() < Math.pow(0.4, Math.floor(timesFeatureAMade / 10.0)))
        {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.isAir() && blockBelowState.isRedstoneConductor(world, posBelow) && world instanceof ServerLevel serverLevel)
            {
                Registry<PlacedFeature> placedFeatureRegistry = serverLevel.registryAccess().registryOrThrow(Registries.PLACED_FEATURE);
                boolean success = placedFeatureRegistry.get(BROWN_MUSHROOM_NETHER).place(serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureAMade++;
            }
        }
        if(random.nextDouble() < Math.pow(0.3, Math.floor(timesFeatureBMade / 10.0))) {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.isAir() && blockBelowState.isRedstoneConductor(world, posBelow) && world instanceof ServerLevel serverLevel)
            {
                Registry<PlacedFeature> placedFeatureRegistry = serverLevel.registryAccess().registryOrThrow(Registries.PLACED_FEATURE);
                boolean success = placedFeatureRegistry.get(RED_MUSHROOM_NETHER).place(serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureBMade++;
            }
        }
    }
    public void createSoul(Level world, PandorasBoxEntity entity, RandomSource random, int pass, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.END_STONE, Blocks.MYCELIUM);
        blocks.addAll(PandorasBox.flowers, PandorasBox.logs, PandorasBox.leaves);
        misc.addAll(PandorasBox.terracotta);

        if (pass == 0) {
            if (isBlockAnyOf(block, Blocks.COBBLESTONE, Blocks.ICE, Blocks.WATER, Blocks.OBSIDIAN)) {
                Optional<Integer> integer = blockState.getOptionalValue(LiquidBlock.LEVEL);
                BlockState blockState2 = Blocks.LAVA.defaultBlockState();
                if(integer.isPresent()) {
                    blockState2 = blockState2.setValue(LiquidBlock.LEVEL, integer.get());
                }
                setBlockSafe(world, pos, blockState2);
            } else if (isBlockAnyOf(block, blocks)) {
                setBlockToAirSafe(world, pos);
            } else if (isBlockAnyOf(block, Blocks.SAND, Blocks.CLAY)) {
                setBlockSafe(world, pos, Blocks.SOUL_SAND.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.COAL_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GOLD_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_GOLD_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, misc)) {
                setBlockSafe(world, pos, Blocks.SOUL_SOIL.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE)) {
                setBlockSafe(world, pos, Blocks.NETHERRACK.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GRANITE)) {
                setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
            } else if (world.getBlockState(pos).isAir()) {
                boolean bl = !isBlockAnyOf(world.getBlockState(pos.below()).getBlock(), Blocks.BONE_BLOCK) ? random.nextInt(40) == 0 : random.nextInt(20) == 0;
                if (world instanceof ServerLevel && random.nextInt(25) == 0) {
                    if (world.random.nextFloat() < 0.9f) {
                        setBlockSafe(world, pos, Blocks.SOUL_FIRE.defaultBlockState());
                    } else {
                        setBlockSafe(world, pos, Blocks.GLOWSTONE.defaultBlockState());
                    }
                }else if(!world.getBlockState(pos.below()).isAir() && !isBlockAnyOf(world.getBlockState(pos.below()).getBlock(), Blocks.GLOWSTONE) && bl) {
                    setBlockSafe(world, pos, Blocks.BONE_BLOCK.defaultBlockState());
                }
            }
        } else {
            ArrayListExtensions<Entity> entities = new ArrayListExtensions<>();
            entities.addAll(
                    lazilySpawnEntity(world, entity, random, "skeleton", 1.0f / (15 * 15), pos),
                    lazilySpawnEntity(world, entity, random, "zombified_piglin", 1.0f / (15 * 15), pos));

            for (Entity entity1 : entities) {
                canSpawnEntity(world, blockState, pos, entity1);
            }

            if (canSpawnFlyingEntity(world, blockState, pos)) {
                lazilySpawnFlyingEntity(world, entity, random, "ghast", 1.0f / (50 * 50 * 50), pos);
                lazilySpawnFlyingEntity(world, entity, random, "blaze", 1.0f / (50 * 50 * 50), pos);
            }
        }
    }
    public void createCrimson(Level world, PandorasBoxEntity entity, RandomSource random, int pass, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        ArrayListExtensions<Block> flowers = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.END_STONE, Blocks.MYCELIUM, Blocks.SAND);
        blocks.addAll(PandorasBox.logs, PandorasBox.leaves);
        misc.addAll(PandorasBox.terracotta);
        flowers.addAll(PandorasBox.flowers);

        if (pass == 0) {
            if (isBlockAnyOf(block, Blocks.COBBLESTONE, Blocks.ICE, Blocks.WATER, Blocks.OBSIDIAN)) {
                Optional<Integer> integer = blockState.getOptionalValue(LiquidBlock.LEVEL);
                BlockState blockState2 = Blocks.LAVA.defaultBlockState();
                if(integer.isPresent()) {
                    blockState2 = blockState2.setValue(LiquidBlock.LEVEL, integer.get());
                }
                setBlockSafe(world, pos, blockState2);
            } else if (isBlockAnyOf(block, blocks)) {
                setBlockToAirSafe(world, pos);
            } else if (isBlockAnyOf(block, Blocks.CLAY)) {
                setBlockSafe(world, pos, Blocks.MAGMA_BLOCK.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.COAL_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GOLD_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_GOLD_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, misc)) {
                if(random.nextDouble() < 0.2 || !world.getBlockState(pos.above()).isAir())
                    setBlockSafe(world, pos, Blocks.NETHERRACK.defaultBlockState());
                else
                    setBlockSafe(world, pos, Blocks.CRIMSON_NYLIUM.defaultBlockState());
            } else if (isBlockAnyOf(block, flowers)) {
                if(random.nextDouble() < 0.5)
                    setBlockSafe(world, pos, Blocks.CRIMSON_ROOTS.defaultBlockState());
                else
                    setBlockSafe(world, pos, Blocks.CRIMSON_FUNGUS.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GRANITE)) {
                setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
            } else if (world.getBlockState(pos).isAir()) {
                if (world instanceof ServerLevel && random.nextInt(25) == 0) {
                    if (world.random.nextFloat() < 0.9f) {
                        setBlockSafe(world, pos, Blocks.FIRE.defaultBlockState());
                    } else {
                        setBlockSafe(world, pos, Blocks.GLOWSTONE.defaultBlockState());
                    }
                }
            }
        } else {
            ArrayListExtensions<Entity> entities = new ArrayListExtensions<>();
            entities.addAll(
                    lazilySpawnEntity(world, entity, random, "piglin", 1.0f / (10 * 10), pos),
                    lazilySpawnEntity(world, entity, random, "zombified_piglin", 1.0f / (25 * 25), pos),
                    lazilySpawnEntity(world, entity, random, "hoglin", 1.0f / (10 * 10), pos));

            for (Entity entity1 : entities) {
                canSpawnEntity(world, blockState, pos, entity1);
            }

            if (canSpawnFlyingEntity(world, blockState, pos)) {
                lazilySpawnFlyingEntity(world, entity, random, "ghast", 1.0f / (50 * 50 * 50), pos);
                lazilySpawnFlyingEntity(world, entity, random, "blaze", 1.0f / (50 * 50 * 50), pos);
            }
        }
        if (random.nextDouble() < Math.pow(0.4, Math.floor(timesFeatureAMade / 8.0)))
        {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.isAir() && !blockBelowState.is(Blocks.NETHER_WART_BLOCK) && blockBelowState.isRedstoneConductor(world, posBelow) && world instanceof ServerLevel serverWorld)
            {
                Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = serverWorld.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                setBlockSafe(world, posBelow, Blocks.CRIMSON_NYLIUM.defaultBlockState());
                boolean success = configuredFeatureRegistry.get(CRIMSON_FUNGUS_PLANTED).place(serverWorld, serverWorld.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureAMade++;
            }
        }
        if (random.nextDouble() < Math.pow(0.6, Math.floor(timesFeatureBMade / 10.0)))
        {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.isAir() && !blockBelowState.is(Blocks.NETHER_WART_BLOCK) && blockBelowState.isRedstoneConductor(world, posBelow) && world instanceof ServerLevel serverLevel)
            {
                Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = serverLevel.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                setBlockSafe(world, posBelow, Blocks.CRIMSON_NYLIUM.defaultBlockState());
                boolean success = configuredFeatureRegistry.get(CRIMSON_FOREST_VEGETATION).place(serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureBMade++;
            }
        }
    }
    public void createWarped(Level world, PandorasBoxEntity entity, RandomSource random, int pass, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        ArrayListExtensions<Block> flowers = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.STONE, Blocks.ANDESITE, Blocks.GRANITE, Blocks.END_STONE, Blocks.MYCELIUM, Blocks.SAND);
        blocks.addAll(PandorasBox.logs, PandorasBox.leaves);
        misc.addAll(PandorasBox.terracotta);
        flowers.addAll(PandorasBox.flowers);

        if (pass == 0) {
            if (isBlockAnyOf(block, Blocks.COBBLESTONE, Blocks.ICE, Blocks.WATER, Blocks.OBSIDIAN)) {
                Optional<Integer> integer = blockState.getOptionalValue(LiquidBlock.LEVEL);
                BlockState blockState2 = Blocks.LAVA.defaultBlockState();
                if(integer.isPresent()) {
                    blockState2 = blockState2.setValue(LiquidBlock.LEVEL, integer.get());
                }
                setBlockSafe(world, pos, blockState2);
            } else if (isBlockAnyOf(block, blocks)) {
                setBlockToAirSafe(world, pos);
            } else if (isBlockAnyOf(block, Blocks.CLAY)) {
                setBlockSafe(world, pos, Blocks.MAGMA_BLOCK.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.COAL_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GOLD_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_GOLD_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, misc)) {
                if(random.nextDouble() < 0.2 || !world.getBlockState(pos.above()).isAir())
                    setBlockSafe(world, pos, Blocks.NETHERRACK.defaultBlockState());
                else
                    setBlockSafe(world, pos, Blocks.WARPED_NYLIUM.defaultBlockState());
            } else if (isBlockAnyOf(block, flowers)) {
                if(random.nextDouble() < 0.5)
                    setBlockSafe(world, pos, Blocks.WARPED_ROOTS.defaultBlockState());
                else
                    setBlockSafe(world, pos, Blocks.WARPED_FUNGUS.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GRANITE)) {
                setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
            } else if (world.getBlockState(pos).isAir()) {
                if (world instanceof ServerLevel && random.nextInt(25) == 0) {
                    if (world.random.nextFloat() < 0.9f) {
                        setBlockSafe(world, pos, Blocks.FIRE.defaultBlockState());
                    } else {
                        setBlockSafe(world, pos, Blocks.GLOWSTONE.defaultBlockState());
                    }
                }
            }
        } else {
            ArrayListExtensions<Entity> entities = new ArrayListExtensions<>();
            entities.addAll(
                    lazilySpawnEntity(world, entity, random, "enderman", 1.0f / (15 * 15), pos));

            for (Entity entity1 : entities) {
                canSpawnEntity(world, blockState, pos, entity1);
            }
        }
        if (random.nextDouble() < Math.pow(0.4, Math.floor(timesFeatureAMade / 8.0)))
        {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.isAir() && !blockBelowState.is(Blocks.NETHER_WART_BLOCK) && blockBelowState.isRedstoneConductor(world, posBelow) && world instanceof ServerLevel serverWorld)
            {
                Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = serverWorld.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                setBlockSafe(world, posBelow, Blocks.WARPED_NYLIUM.defaultBlockState());
                boolean success = configuredFeatureRegistry.get(WARPED_FUNGUS_PLANTED).place(serverWorld, serverWorld.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureAMade++;
            }
        }
        if (random.nextDouble() < Math.pow(0.6, Math.floor(timesFeatureBMade / 10.0)))
        {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.isAir() && !blockBelowState.is(Blocks.WARPED_WART_BLOCK) && blockBelowState.isRedstoneConductor(world, posBelow) && world instanceof ServerLevel serverLevel)
            {
                Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = serverLevel.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                setBlockSafe(world, posBelow, Blocks.WARPED_NYLIUM.defaultBlockState());
                boolean success = configuredFeatureRegistry.get(WARPED_FOREST_VEGETION).place(serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureBMade++;
            }
        }
    }
    public void createDeltas(Level world, PandorasBoxEntity entity, RandomSource random, int pass, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.END_STONE, Blocks.MYCELIUM);
        blocks.addAll(PandorasBox.flowers, PandorasBox.logs, PandorasBox.leaves);
        misc.addAll(PandorasBox.terracotta);

        if (pass == 0) {
            if (isBlockAnyOf(block, Blocks.COBBLESTONE, Blocks.ICE, Blocks.WATER, Blocks.OBSIDIAN)) {
                Optional<Integer> integer = blockState.getOptionalValue(LiquidBlock.LEVEL);
                BlockState blockState2 = Blocks.LAVA.defaultBlockState();
                if(integer.isPresent()) {
                    blockState2 = blockState2.setValue(LiquidBlock.LEVEL, integer.get());
                }
                setBlockSafe(world, pos, blockState2);
            } else if (isBlockAnyOf(block, blocks)) {
                setBlockToAirSafe(world, pos);
            } else if (isBlockAnyOf(block, Blocks.CLAY)) {
                setBlockSafe(world, pos, Blocks.MAGMA_BLOCK.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.SAND)) {
                setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.COAL_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GOLD_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_GOLD_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, misc)) {
                setBlockSafe(world, pos, Blocks.BASALT.defaultBlockState());
            }  else if (isBlockAnyOf(block, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE)) {
                setBlockSafe(world, pos, Blocks.NETHERRACK.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GRANITE)) {
                setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
            } else if (world.getBlockState(pos).isAir()) {
                if (world instanceof ServerLevel && random.nextInt(25) == 0) {
                    if (world.random.nextFloat() < 0.9f) {
                        setBlockSafe(world, pos, Blocks.FIRE.defaultBlockState());
                    } else {
                        setBlockSafe(world, pos, Blocks.GLOWSTONE.defaultBlockState());
                    }
                }
            }
        } else {
            ArrayListExtensions<Entity> entities = new ArrayListExtensions<>();
            entities.addAll(
                    lazilySpawnEntity(world, entity, random, "magma_cube", 1.0f / (15 * 15), pos));

            for (Entity entity1 : entities) {
                canSpawnEntity(world, blockState, pos, entity1);
            }

            if (canSpawnFlyingEntity(world, blockState, pos)) {
                lazilySpawnFlyingEntity(world, entity, random, "ghast", 1.0f / (50 * 50 * 50), pos);
                lazilySpawnFlyingEntity(world, entity, random, "blaze", 1.0f / (50 * 50 * 50), pos);
            }
        }
        if (random.nextDouble() < Math.pow(0.8, Math.floor(timesFeatureAMade / 4.0)))
        {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.isAir() && blockBelowState.isRedstoneConductor(world, posBelow) && world instanceof ServerLevel serverLevel)
            {
                Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = serverLevel.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                boolean success = configuredFeatureRegistry.get(SMALL_BASALT_COLUMNS).place(serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureAMade++;
            }
        }
        if(random.nextDouble() < Math.pow(0.3, Math.floor(timesFeatureBMade / 4.0))) {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.isAir() && blockBelowState.isRedstoneConductor(world, posBelow) && world instanceof ServerLevel serverLevel)
            {
                Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = serverLevel.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                boolean success = configuredFeatureRegistry.get(LARGE_BASALT_COLUMNS).place(serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureBMade++;
            }
        }
    }

    @Override
    public void readFromNBT(CompoundTag compound) {
        super.readFromNBT(compound);
        biome = compound.getString("biome");
        timesFeatureAMade = compound.getInt("featureACount");
        timesFeatureBMade = compound.getInt("featureBCount");
    }

    @Override
    public void writeToNBT(CompoundTag compound) {
        super.writeToNBT(compound);
        if(biome != null) {
            compound.putString("biome", biome);
        }
        compound.putInt("featureACount", timesFeatureAMade);
        compound.putInt("featureBCount", timesFeatureBMade);
    }
}
