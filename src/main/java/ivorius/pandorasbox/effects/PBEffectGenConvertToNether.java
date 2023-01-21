/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.weighted.WeightedSelector;
import ivorius.pandorasbox.weighted.WeightedSelector.SimpleItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

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
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range)
    {
        switch (biome) {
            case "wastes": {
                createWastes(world, entity, random, pass, pos);
                break;
            }
            case "soul_sand_valley": {
                createSoul(world, entity, random, pass, pos);
                break;
            }
            case "crimson": {
                createCrimson(world, entity, random, pass, pos);
                break;
            }
            case "warped": {
                createWarped(world, entity, random, pass, pos);
                break;
            }
            case "deltas": {
                createDeltas(world, entity, random, pass, pos);
                break;
            }
        }
    }
    public void createWastes(World world, PandorasBoxEntity entity, Random random, int pass, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.END_STONE, Blocks.MYCELIUM);
        for (Block block1 : ForgeRegistries.BLOCKS) {
            if (BlockTags.LOGS.contains(block1) || BlockTags.LEAVES.contains(block1) || BlockTags.SMALL_FLOWERS.contains(block1)) {
                blocks.add(block1);
            }
            if (block1.getRegistryName().getPath().endsWith("terracotta")) {
                misc.add(block1);
            }
        }

        if (pass == 0) {
            if (isBlockAnyOf(block, Blocks.COBBLESTONE, Blocks.ICE, Blocks.WATER, Blocks.OBSIDIAN)) {
                setBlockSafe(world, pos, Blocks.LAVA.defaultBlockState());
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
            } else if (world.getBlockState(pos).isAir(world, pos)) {
                if (world instanceof ServerWorld && random.nextInt(15) == 0) {
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
        if (random.nextDouble() < Math.pow(0.4, Math.floor(timesFeatureAMade / 16.0)))
        {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.getMaterial() == Material.AIR && blockBelowState.isRedstoneConductor(world, posBelow) && world instanceof ServerWorld)
            {
                ServerWorld serverWorld = (ServerWorld) world;
                boolean success = Features.BROWN_MUSHROOM_NETHER.place(serverWorld, serverWorld.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureAMade++;
            }
        }
        if(random.nextDouble() < Math.pow(0.3, Math.floor(timesFeatureBMade / 16.0))) {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.getMaterial() == Material.AIR && blockBelowState.isRedstoneConductor(world, posBelow) && world instanceof ServerWorld)
            {
                ServerWorld serverWorld = (ServerWorld) world;
                boolean success = Features.RED_MUSHROOM_NETHER.place(serverWorld, serverWorld.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureBMade++;
            }
        }
    }
    public void createSoul(World world, PandorasBoxEntity entity, Random random, int pass, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.END_STONE, Blocks.MYCELIUM);
        for (Block block1 : ForgeRegistries.BLOCKS) {
            if (BlockTags.LOGS.contains(block1) || BlockTags.LEAVES.contains(block1) || BlockTags.SMALL_FLOWERS.contains(block1)) {
                blocks.add(block1);
            }
            if (block1.getRegistryName().getPath().endsWith("terracotta")) {
                misc.add(block1);
            }
        }

        if (pass == 0) {
            if (isBlockAnyOf(block, Blocks.COBBLESTONE, Blocks.ICE, Blocks.WATER, Blocks.OBSIDIAN)) {
                setBlockSafe(world, pos, Blocks.LAVA.defaultBlockState());
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
            } else if (world.getBlockState(pos).isAir(world, pos)) {
                if (world instanceof ServerWorld && random.nextInt(15) == 0) {
                    if (world.random.nextFloat() < 0.9f) {
                        setBlockSafe(world, pos, Blocks.SOUL_FIRE.defaultBlockState());
                    } else {
                        setBlockSafe(world, pos, Blocks.GLOWSTONE.defaultBlockState());
                    }
                }else if(!world.getBlockState(pos.below()).isAir(world, pos.below()) && random.nextInt(20) == 0) {
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
    public void createCrimson(World world, PandorasBoxEntity entity, Random random, int pass, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        ArrayListExtensions<Block> flowers = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.END_STONE, Blocks.MYCELIUM, Blocks.SAND);
        for (Block block1 : ForgeRegistries.BLOCKS) {
            if (BlockTags.LOGS.contains(block1) || BlockTags.LEAVES.contains(block1)) {
                blocks.add(block1);
            }
            if(BlockTags.SMALL_FLOWERS.contains(block1)) {
                flowers.add(block1);
            }
            if (block1.getRegistryName().getPath().endsWith("terracotta")) {
                misc.add(block1);
            }
        }

        if (pass == 0) {
            if (isBlockAnyOf(block, Blocks.COBBLESTONE, Blocks.ICE, Blocks.WATER, Blocks.OBSIDIAN)) {
                setBlockSafe(world, pos, Blocks.LAVA.defaultBlockState());
            } else if (isBlockAnyOf(block, blocks)) {
                setBlockToAirSafe(world, pos);
            } else if (isBlockAnyOf(block, Blocks.CLAY)) {
                setBlockSafe(world, pos, Blocks.MAGMA_BLOCK.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.COAL_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GOLD_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_GOLD_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, misc)) {
                if(random.nextDouble() < 0.2 || !world.getBlockState(pos.above()).isAir(world, pos.above()))
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
            } else if (world.getBlockState(pos).isAir(world, pos)) {
                if (world instanceof ServerWorld && random.nextInt(15) == 0) {
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

            if (blockState.getMaterial() == Material.AIR && blockBelowState.isRedstoneConductor(world, posBelow) && world instanceof ServerWorld)
            {
                setBlockSafe(world, posBelow, Blocks.CRIMSON_NYLIUM.defaultBlockState());
                ServerWorld serverWorld = (ServerWorld) world;
                boolean success = Features.CRIMSON_FUNGI_PLANTED.place(serverWorld, serverWorld.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureAMade++;
            }
        }
        if (random.nextDouble() < Math.pow(0.6, Math.floor(timesFeatureBMade / 16.0)))
        {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.getMaterial() == Material.AIR && blockBelowState.isRedstoneConductor(world, posBelow) && world instanceof ServerWorld)
            {
                setBlockSafe(world, posBelow, Blocks.CRIMSON_NYLIUM.defaultBlockState());
                ServerWorld serverWorld = (ServerWorld) world;
                boolean success = Features.CRIMSON_FOREST_VEGETATION.place(serverWorld, serverWorld.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureBMade++;
            }
        }
    }
    public void createWarped(World world, PandorasBoxEntity entity, Random random, int pass, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        ArrayListExtensions<Block> flowers = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.STONE, Blocks.ANDESITE, Blocks.GRANITE, Blocks.END_STONE, Blocks.MYCELIUM, Blocks.SAND);
        for (Block block1 : ForgeRegistries.BLOCKS) {
            if (BlockTags.LOGS.contains(block1) || BlockTags.LEAVES.contains(block1)) {
                blocks.add(block1);
            }
            if(BlockTags.SMALL_FLOWERS.contains(block1)) {
                flowers.add(block1);
            }
            if (block1.getRegistryName().getPath().endsWith("terracotta")) {
                misc.add(block1);
            }
        }

        if (pass == 0) {
            if (isBlockAnyOf(block, Blocks.COBBLESTONE, Blocks.ICE, Blocks.WATER, Blocks.OBSIDIAN)) {
                setBlockSafe(world, pos, Blocks.LAVA.defaultBlockState());
            } else if (isBlockAnyOf(block, blocks)) {
                setBlockToAirSafe(world, pos);
            } else if (isBlockAnyOf(block, Blocks.CLAY)) {
                setBlockSafe(world, pos, Blocks.MAGMA_BLOCK.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.COAL_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GOLD_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_GOLD_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, misc)) {
                if(random.nextDouble() < 0.2 || !world.getBlockState(pos.above()).isAir(world, pos.above()))
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
            } else if (world.getBlockState(pos).isAir(world, pos)) {
                if (world instanceof ServerWorld && random.nextInt(15) == 0) {
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

            if (blockState.getMaterial() == Material.AIR && blockBelowState.isRedstoneConductor(world, posBelow) && world instanceof ServerWorld)
            {
                setBlockSafe(world, posBelow, Blocks.WARPED_NYLIUM.defaultBlockState());
                ServerWorld serverWorld = (ServerWorld) world;
                boolean success = Features.WARPED_FUNGI_PLANTED.place(serverWorld, serverWorld.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureAMade++;
            }
        }
        if (random.nextDouble() < Math.pow(0.6, Math.floor(timesFeatureBMade / 16.0)))
        {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.getMaterial() == Material.AIR && blockBelowState.isRedstoneConductor(world, posBelow) && world instanceof ServerWorld)
            {
                setBlockSafe(world, posBelow, Blocks.WARPED_NYLIUM.defaultBlockState());
                ServerWorld serverWorld = (ServerWorld) world;
                boolean success = Features.WARPED_FOREST_VEGETATION.place(serverWorld, serverWorld.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureBMade++;
            }
        }
    }
    public void createDeltas(World world, PandorasBoxEntity entity, Random random, int pass, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.END_STONE, Blocks.MYCELIUM);
        for (Block block1 : ForgeRegistries.BLOCKS) {
            if (BlockTags.LOGS.contains(block1) || BlockTags.LEAVES.contains(block1) || BlockTags.SMALL_FLOWERS.contains(block1)) {
                blocks.add(block1);
            }
            if (block1.getRegistryName().getPath().endsWith("terracotta")) {
                misc.add(block1);
            }
        }

        if (pass == 0) {
            if (isBlockAnyOf(block, Blocks.COBBLESTONE, Blocks.ICE, Blocks.WATER, Blocks.OBSIDIAN)) {
                setBlockSafe(world, pos, Blocks.LAVA.defaultBlockState());
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
            } else if (world.getBlockState(pos).isAir(world, pos)) {
                if (world instanceof ServerWorld && random.nextInt(15) == 0) {
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

            if (blockState.getMaterial() == Material.AIR && blockBelowState.isRedstoneConductor(world, posBelow) && world instanceof ServerWorld)
            {
                ServerWorld serverWorld = (ServerWorld) world;
                boolean success = Features.SMALL_BASALT_COLUMNS.place(serverWorld, serverWorld.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureAMade++;
            }
        }
        if(random.nextDouble() < Math.pow(0.3, Math.floor(timesFeatureBMade / 8.0))) {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.getMaterial() == Material.AIR && blockBelowState.isRedstoneConductor(world, posBelow) && world instanceof ServerWorld)
            {
                ServerWorld serverWorld = (ServerWorld) world;
                boolean success = Features.LARGE_BASALT_COLUMNS.place(serverWorld, serverWorld.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureBMade++;
            }
        }
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        super.readFromNBT(compound);
        biome = compound.getString("biome");
        timesFeatureAMade = compound.getInt("featureACount");
        timesFeatureBMade = compound.getInt("featureBCount");
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        super.writeToNBT(compound);
        if(biome != null) {
            compound.putString("biome", biome);
        }
        compound.putInt("featureACount", timesFeatureAMade);
        compound.putInt("featureBCount", timesFeatureBMade);
    }
}
