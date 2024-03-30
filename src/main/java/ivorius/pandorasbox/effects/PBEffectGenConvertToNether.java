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
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.server.ServerWorld;

import java.util.Optional;
import java.util.Random;

import static net.minecraft.world.gen.feature.Features.GLOWSTONE_EXTRA;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToNether extends PBEffectGenerate {
    private String biome;
    private double discardNetherrackChance;
    private int timesFeatureAMade = 0;
    private int timesFeatureBMade = 0;
    public PBEffectGenConvertToNether() {}

    public PBEffectGenConvertToNether(int time, double range, double discardChance, int unifiedSeed, String biome) {
        super(time, range, 2, unifiedSeed);
        this.discardNetherrackChance = discardChance;
        this.biome = biome;
    }

    @Override
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range) {
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            float newRatio = getRatioDone(entity.getEffectTicksExisted() + 1);
            switch (biome) {
                case "wastes": {
                    createWastes(serverWorld, entity, random, pass, newRatio, pos);
                    break;
                }
                case "soul_sand_valley": {
                    createSoul(serverWorld, entity, random, pass, newRatio, pos);
                    break;
                }
                case "crimson": {
                    createCrimson(serverWorld, entity, random, pass, newRatio, pos);
                    break;
                }
                case "warped": {
                    createWarped(serverWorld, entity, random, pass, newRatio, pos);
                    break;
                }
                case "deltas": {
                    createDeltas(serverWorld, entity, random, pass, newRatio, pos);
                    break;
                }
            }
        }
    }
    public void createWastes(ServerWorld world, PandorasBoxEntity entity, Random random, int pass, float newRatio, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.END_STONE, Blocks.MYCELIUM);
        blocks.addAll(PandorasBox.flowers, PandorasBox.logs, PandorasBox.leaves);
        misc.addAll(PandorasBox.terracotta);

        if (pass == 0) {
            if (random.nextDouble() < (discardNetherrackChance / 100) * Math.pow(1 + (discardNetherrackChance * 2), newRatio * 100)) {
                return;
            } else if (isBlockAnyOf(block, Blocks.COBBLESTONE, Blocks.ICE, Blocks.WATER, Blocks.OBSIDIAN)) {
                Optional<Integer> integer = blockState.getOptionalValue(FlowingFluidBlock.LEVEL);
                BlockState blockState2 = Blocks.LAVA.defaultBlockState();
                if(integer.isPresent()) {
                    blockState2 = blockState2.setValue(FlowingFluidBlock.LEVEL, integer.get());
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
            } else if (isBlockAnyOf(block, Blocks.SAND, Blocks.RED_SAND)) {
                setBlockSafe(world, pos, Blocks.SOUL_SAND.defaultBlockState());
            } else if (isBlockAnyOf(block, misc)) {
                setBlockSafe(world, pos, Blocks.NETHERRACK.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GRANITE)) {
                setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
            } else if (world.getBlockState(pos).isAir(world, pos)) {
                if (random.nextInt(25) == 0) {
                    if (world.random.nextFloat() < 0.99f) {
                        if(world.getBlockState(pos.below()).is(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
                            setBlockSafe(world, pos, Blocks.SOUL_FIRE.defaultBlockState());
                        } else if(!world.getBlockState(pos.below()).isAir())
                            setBlockSafe(world, pos, Blocks.FIRE.defaultBlockState());
                    } else {
                        setBlockSafe(world, pos, Blocks.GLOWSTONE.defaultBlockState());
                    }
                } else if(!world.getBlockState(pos.above()).isAir() && random.nextFloat() < 0.02) {
                    createGlowstoneBlobs(world, pos, random);
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
        if (random.nextDouble() < Math.pow(0.4, Math.floor(timesFeatureAMade / 4.0))) {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.getMaterial() == Material.AIR && blockBelowState.isRedstoneConductor(world, posBelow)) {
                boolean success = Features.BROWN_MUSHROOM_NETHER.place(world, world.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureAMade++;
            }
        }
        if (random.nextDouble() < Math.pow(0.3, Math.floor(timesFeatureBMade / 4.0))) {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.getMaterial() == Material.AIR && blockBelowState.isRedstoneConductor(world, posBelow)) {
                boolean success = Features.RED_MUSHROOM_NETHER.place(world, world.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureBMade++;
            }
        }
    }
    public void createSoul(ServerWorld world, PandorasBoxEntity entity, Random random, int pass, float newRatio, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.END_STONE, Blocks.MYCELIUM);
        blocks.addAll(PandorasBox.flowers, PandorasBox.logs, PandorasBox.leaves);
        misc.addAll(PandorasBox.terracotta);

        if (pass == 0) {
            if (random.nextDouble() < (discardNetherrackChance / 100) * Math.pow(1 + (discardNetherrackChance * 2), newRatio * 100)) return;
            if (isBlockAnyOf(block, Blocks.COBBLESTONE, Blocks.ICE, Blocks.WATER, Blocks.OBSIDIAN)) {
                Optional<Integer> integer = blockState.getOptionalValue(FlowingFluidBlock.LEVEL);
                BlockState blockState2 = Blocks.LAVA.defaultBlockState();
                if(integer.isPresent()) {
                    blockState2 = blockState2.setValue(FlowingFluidBlock.LEVEL, integer.get());
                }
                setBlockSafe(world, pos, blockState2);
            } else if (isBlockAnyOf(block, blocks)) {
                setBlockToAirSafe(world, pos);
            } else if (isBlockAnyOf(block, Blocks.SAND, Blocks.RED_SAND, Blocks.CLAY)) {
                setBlockSafe(world, pos, Blocks.SOUL_SAND.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.COAL_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GOLD_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_GOLD_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, misc)) {
                setBlockSafe(world, pos, Blocks.SOUL_SOIL.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE)) {
                setBlockSafe(world, pos, Blocks.NETHERRACK.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GRANITE)) {
                setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
            } else if (world.getBlockState(pos).isAir(world, pos)) {
                boolean bl = !isBlockAnyOf(world.getBlockState(pos.below()).getBlock(), Blocks.BONE_BLOCK) ? random.nextInt(40) == 0 : random.nextInt(20) == 0;
                if (random.nextInt(25) == 0) {
                    if (world.random.nextFloat() < 0.99f) {
                        if(world.getBlockState(pos.below()).is(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
                            setBlockSafe(world, pos, Blocks.SOUL_FIRE.defaultBlockState());
                        } else if(!world.getBlockState(pos.below()).isAir())
                            setBlockSafe(world, pos, Blocks.FIRE.defaultBlockState());
                    } else {
                        setBlockSafe(world, pos, Blocks.GLOWSTONE.defaultBlockState());
                    }
                } else if(!world.getBlockState(pos.above()).isAir() && random.nextFloat() < 0.02) {
                    createGlowstoneBlobs(world, pos, random);
                } else if(!world.getBlockState(pos.below()).isAir(world, pos.below()) && !isBlockAnyOf(world.getBlockState(pos.below()).getBlock(), Blocks.GLOWSTONE) && bl) {
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
    public void createCrimson(ServerWorld world, PandorasBoxEntity entity, Random random, int pass, float newRatio, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        ArrayListExtensions<Block> flowers = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.END_STONE, Blocks.MYCELIUM, Blocks.SAND, Blocks.RED_SAND);
        blocks.addAll(PandorasBox.logs, PandorasBox.leaves);
        misc.addAll(PandorasBox.terracotta);
        flowers.addAll(PandorasBox.flowers);
        blocks.removeAll(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM, Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM);

        if (pass == 0) {
            if (random.nextDouble() < (discardNetherrackChance / 100) * Math.pow(1 + (discardNetherrackChance * 2), newRatio * 100)) {
                return;
            } else if (isBlockAnyOf(block, Blocks.COBBLESTONE, Blocks.ICE, Blocks.WATER, Blocks.OBSIDIAN)) {
                Optional<Integer> integer = blockState.getOptionalValue(FlowingFluidBlock.LEVEL);
                BlockState blockState2 = Blocks.LAVA.defaultBlockState();
                if(integer.isPresent()) {
                    blockState2 = blockState2.setValue(FlowingFluidBlock.LEVEL, integer.get());
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
                if (random.nextInt(25) == 0) {
                    if (world.random.nextFloat() < 0.99f) {
                        if(world.getBlockState(pos.below()).is(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
                            setBlockSafe(world, pos, Blocks.SOUL_FIRE.defaultBlockState());
                        } else if(!world.getBlockState(pos.below()).isAir())
                            setBlockSafe(world, pos, Blocks.FIRE.defaultBlockState());
                    } else {
                        setBlockSafe(world, pos, Blocks.GLOWSTONE.defaultBlockState());
                    }
                } else if(!world.getBlockState(pos.above()).isAir() && random.nextFloat() < 0.02) {
                    createGlowstoneBlobs(world, pos, random);
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
        if (random.nextDouble() < Math.pow(0.4, Math.floor(timesFeatureAMade / 8.0))) {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.getMaterial() == Material.AIR && !blockBelowState.is(Blocks.NETHER_WART_BLOCK) && blockBelowState.isRedstoneConductor(world, posBelow)) {
                setBlockSafe(world, posBelow, Blocks.CRIMSON_NYLIUM.defaultBlockState());
                boolean success = Features.CRIMSON_FUNGI_PLANTED.place(world, world.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureAMade++;
            }
        }
        if (random.nextDouble() < Math.pow(0.6, Math.floor(timesFeatureBMade / 10.0))) {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.getMaterial() == Material.AIR && !blockBelowState.is(Blocks.NETHER_WART_BLOCK) && blockBelowState.isRedstoneConductor(world, posBelow)) {
                setBlockSafe(world, posBelow, Blocks.CRIMSON_NYLIUM.defaultBlockState());
                boolean success = Features.CRIMSON_FOREST_VEGETATION.place(world, world.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureBMade++;
            }
        }
    }
    public void createWarped(ServerWorld world, PandorasBoxEntity entity, Random random, int pass, float newRatio, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        ArrayListExtensions<Block> flowers = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.STONE, Blocks.ANDESITE, Blocks.GRANITE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.END_STONE, Blocks.MYCELIUM, Blocks.SAND, Blocks.RED_SAND);
        blocks.addAll(PandorasBox.logs, PandorasBox.leaves);
        misc.addAll(PandorasBox.terracotta);
        flowers.addAll(PandorasBox.flowers);
        blocks.removeAll(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM, Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM);

        if (pass == 0) {
            if (random.nextDouble() < (discardNetherrackChance / 100) * Math.pow(1 + (discardNetherrackChance * 2), newRatio * 100)) {
                return;
            } else if (isBlockAnyOf(block, Blocks.COBBLESTONE, Blocks.ICE, Blocks.WATER, Blocks.OBSIDIAN)) {
                Optional<Integer> integer = blockState.getOptionalValue(FlowingFluidBlock.LEVEL);
                BlockState blockState2 = Blocks.LAVA.defaultBlockState();
                if(integer.isPresent()) {
                    blockState2 = blockState2.setValue(FlowingFluidBlock.LEVEL, integer.get());
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
                if (random.nextInt(25) == 0) {
                    if (world.random.nextFloat() < 0.99f) {
                        if(world.getBlockState(pos.below()).is(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
                            setBlockSafe(world, pos, Blocks.SOUL_FIRE.defaultBlockState());
                        } else if(!world.getBlockState(pos.below()).isAir())
                            setBlockSafe(world, pos, Blocks.FIRE.defaultBlockState());
                    } else {
                        setBlockSafe(world, pos, Blocks.GLOWSTONE.defaultBlockState());
                    }
                } else if(!world.getBlockState(pos.above()).isAir() && random.nextFloat() < 0.02) {
                    createGlowstoneBlobs(world, pos, random);
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
        if (random.nextDouble() < Math.pow(0.4, Math.floor(timesFeatureAMade / 8.0))) {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.getMaterial() == Material.AIR && !blockBelowState.is(Blocks.WARPED_WART_BLOCK) && blockBelowState.isRedstoneConductor(world, posBelow)) {
                setBlockSafe(world, posBelow, Blocks.WARPED_NYLIUM.defaultBlockState());
                boolean success = Features.WARPED_FUNGI_PLANTED.place(world, world.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureAMade++;
            }
        }
        if (random.nextDouble() < Math.pow(0.6, Math.floor(timesFeatureBMade / 10.0))) {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.getMaterial() == Material.AIR && !blockBelowState.is(Blocks.WARPED_WART_BLOCK) && blockBelowState.isRedstoneConductor(world, posBelow)) {
                setBlockSafe(world, posBelow, Blocks.WARPED_NYLIUM.defaultBlockState());
                boolean success = Features.WARPED_FOREST_VEGETATION.place(world, world.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureBMade++;
            }
        }
    }
    public void createDeltas(ServerWorld world, PandorasBoxEntity entity, Random random, int pass, float newRatio, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.END_STONE, Blocks.MYCELIUM);
        blocks.addAll(PandorasBox.flowers, PandorasBox.logs, PandorasBox.leaves);
        misc.addAll(PandorasBox.terracotta);

        if (pass == 0) {
            if (random.nextDouble() < (discardNetherrackChance / 100) * Math.pow(1 + (discardNetherrackChance * 2), newRatio * 100)) {
                return;
            } else if (isBlockAnyOf(block, Blocks.COBBLESTONE, Blocks.ICE, Blocks.WATER, Blocks.OBSIDIAN)) {
                Optional<Integer> integer = blockState.getOptionalValue(FlowingFluidBlock.LEVEL);
                BlockState blockState2 = Blocks.LAVA.defaultBlockState();
                if(integer.isPresent()) {
                    blockState2 = blockState2.setValue(FlowingFluidBlock.LEVEL, integer.get());
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
                setBlockSafe(world, pos, Blocks.BASALT.defaultBlockState());
            }  else if (isBlockAnyOf(block, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE)) {
                setBlockSafe(world, pos, Blocks.NETHERRACK.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GRANITE, Blocks.SAND, Blocks.RED_SAND)) {
                setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
            } else if (world.getBlockState(pos).isAir(world, pos)) {
                if (random.nextInt(25) == 0) {
                    if (world.random.nextFloat() < 0.99f) {
                        if(world.getBlockState(pos.below()).is(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
                            setBlockSafe(world, pos, Blocks.SOUL_FIRE.defaultBlockState());
                        } else if(!world.getBlockState(pos.below()).isAir())
                            setBlockSafe(world, pos, Blocks.FIRE.defaultBlockState());
                    } else {
                        setBlockSafe(world, pos, Blocks.GLOWSTONE.defaultBlockState());
                    }
                } else if(!world.getBlockState(pos.above()).isAir() && random.nextFloat() < 0.02) {
                    createGlowstoneBlobs(world, pos, random);
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
        if (random.nextDouble() < Math.pow(0.8, Math.floor(timesFeatureAMade / 4.0))) {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.getMaterial() == Material.AIR && blockBelowState.isRedstoneConductor(world, posBelow)) {
                boolean success = Features.SMALL_BASALT_COLUMNS.place(world, world.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureAMade++;
            }
        }
        if(random.nextDouble() < Math.pow(0.3, Math.floor(timesFeatureBMade / 4.0))) {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.getMaterial() == Material.AIR && blockBelowState.isRedstoneConductor(world, posBelow)) {
                boolean success = Features.LARGE_BASALT_COLUMNS.place(world, world.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureBMade++;
            }
        }
    }
    private void createGlowstoneBlobs(ServerWorld world, BlockPos pos, Random random) {
        GLOWSTONE_EXTRA.place(world, world.getChunkSource().getGenerator(), random, pos);
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        super.readFromNBT(compound);
        biome = compound.getString("biome");
        discardNetherrackChance = compound.getDouble("discardNetherrackChance");
        timesFeatureAMade = compound.getInt("featureACount");
        timesFeatureBMade = compound.getInt("featureBCount");
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        super.writeToNBT(compound);
        if (biome != null) {
            compound.putString("biome", biome);
        }
        compound.putDouble("discardNetherrackChance", discardNetherrackChance);
        compound.putInt("featureACount", timesFeatureAMade);
        compound.putInt("featureBCount", timesFeatureBMade);
    }
}
