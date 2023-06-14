/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.Objects;
import java.util.Optional;

import static net.minecraft.data.worldgen.features.NetherFeatures.*;
import static net.minecraft.data.worldgen.features.TreeFeatures.CRIMSON_FUNGUS_PLANTED;
import static net.minecraft.data.worldgen.features.TreeFeatures.WARPED_FUNGUS_PLANTED;

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
        if(world instanceof ServerLevel serverLevel) {
            switch (biome) {
                case "wastes" -> createWastes(serverLevel, entity, effectCenter, random, pass, pos);
                case "soul_sand_valley" -> createSoul(serverLevel, entity, effectCenter, random, pass, pos);
                case "crimson" -> createCrimson(serverLevel, entity, effectCenter, random, pass, pos);
                case "warped" -> createWarped(serverLevel, entity, effectCenter, random, pass, pos);
                case "deltas" -> createDeltas(serverLevel, entity, effectCenter, random, pass, pos);
            }
        }
    }
    public static BiomeResolver makeResolver(Holder<Biome> biomeHolder) {
        return (x, y, z, climateSampler) -> biomeHolder;
    }
    public void createWastes(ServerLevel world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, int pass, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.MOSS_BLOCK, Blocks.DIRT, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.DEEPSLATE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.END_STONE, Blocks.MYCELIUM);
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
            } else if (isBlockAnyOf(block, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_GOLD_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.SAND, Blocks.RED_SAND)) {
                setBlockSafe(world, pos, Blocks.SOUL_SAND.defaultBlockState());
            } else if (isBlockAnyOf(block, misc)) {
                setBlockSafe(world, pos, Blocks.NETHERRACK.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GRANITE, Blocks.TUFF)) {
                setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
            } else if (world.getBlockState(pos).isAir()) {
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
        if (random.nextDouble() < Math.pow(0.4, Math.floor(timesFeatureAMade / 4.0)))
        {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.isAir() && blockBelowState.isRedstoneConductor(world, posBelow))
            {
                Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                boolean success = Objects.requireNonNull(configuredFeatureRegistry.get(VegetationFeatures.PATCH_BROWN_MUSHROOM)).place(world, world.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureAMade++;
            }
        }
        if(random.nextDouble() < Math.pow(0.3, Math.floor(timesFeatureBMade / 4.0))) {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.isAir() && blockBelowState.isRedstoneConductor(world, posBelow))
            {
                Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                boolean success = Objects.requireNonNull(configuredFeatureRegistry.get(VegetationFeatures.PATCH_RED_MUSHROOM)).place(world, world.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureBMade++;
            }
        }
        changeBiome(Biomes.NETHER_WASTES, pass, effectCenter, world);
    }
    public void createSoul(ServerLevel world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, int pass, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.MOSS_BLOCK, Blocks.DIRT, Blocks.END_STONE, Blocks.MYCELIUM);
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
            } else if (isBlockAnyOf(block, Blocks.SAND, Blocks.CLAY, Blocks.RED_SAND)) {
                setBlockSafe(world, pos, Blocks.SOUL_SAND.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_GOLD_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, misc)) {
                setBlockSafe(world, pos, Blocks.SOUL_SOIL.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.DEEPSLATE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE)) {
                setBlockSafe(world, pos, Blocks.NETHERRACK.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GRANITE, Blocks.TUFF)) {
                setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
            } else if (world.getBlockState(pos).isAir()) {
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
                } else if(!world.getBlockState(pos.below()).isAir() && !isBlockAnyOf(world.getBlockState(pos.below()).getBlock(), Blocks.GLOWSTONE) && bl) {
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
//TODO
//        if(random.nextDouble() < Math.pow(0.6, Math.floor(timesFeatureAMade / 6.0))){
//            ChunkGenerator chunkGenerator = world.getChunkSource().getGenerator();
//            Registry<Structure> structureRegistry = world.registryAccess().registryOrThrow(Registries.STRUCTURE);
//            StructureStart start = Objects.requireNonNull(structureRegistry.get(BuiltinStructures.NETHER_FOSSIL)).generate(world.registryAccess(),
//                    chunkGenerator,
//                    chunkGenerator.getBiomeSource(),
//                    world.getChunkSource().randomState(),
//                    world.getStructureManager(),
//                    world.getSeed(),
//                    new ChunkPos(pos),
//                    0,
//                    world,
//                    biomeHolder -> true);
//            if(!start.isValid()) return;
//            timesFeatureAMade++;
//            BoundingBox boundingbox = start.getBoundingBox();
//            ChunkPos chunkpos = new ChunkPos(SectionPos.blockToSectionCoord(boundingbox.minX()), SectionPos.blockToSectionCoord(boundingbox.minZ()));
//            ChunkPos chunkpos1 = new ChunkPos(SectionPos.blockToSectionCoord(boundingbox.maxX()), SectionPos.blockToSectionCoord(boundingbox.maxZ()));
//            if(!checkLoaded(world, chunkpos, chunkpos1).isPresent()) return;
//            ChunkPos.rangeClosed(chunkpos, chunkpos1).forEach((p_289290_) -> {
//                start.placeInChunk(world, world.structureManager(), world.getChunkSource().getGenerator(), world.getRandom(), new BoundingBox(p_289290_.getMinBlockX(), world.getMinBuildHeight(), p_289290_.getMinBlockZ(), p_289290_.getMaxBlockX(), world.getMaxBuildHeight(), p_289290_.getMaxBlockZ()), p_289290_);
//            });
//        }
        changeBiome(Biomes.SOUL_SAND_VALLEY, pass, effectCenter, world);
    }

    private void createGlowstoneBlobs(ServerLevel world, BlockPos pos, RandomSource random) {
        BlockPos posBelow = pos.below();
        BlockState blockBelowState = world.getBlockState(posBelow);

        if (blockBelowState.isRedstoneConductor(world, posBelow))
        {
            Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
            Objects.requireNonNull(configuredFeatureRegistry.get(GLOWSTONE_EXTRA)).place(world, world.getChunkSource().getGenerator(), random, pos);
        }
    }

    public void createCrimson(ServerLevel world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, int pass, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        ArrayListExtensions<Block> flowers = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.MOSS_BLOCK, Blocks.DIRT, Blocks.STONE, Blocks.ANDESITE, Blocks.DEEPSLATE, Blocks.DIORITE, Blocks.END_STONE, Blocks.MYCELIUM, Blocks.SAND, Blocks.SANDSTONE, Blocks.RED_SAND, Blocks.RED_SANDSTONE);
        blocks.addAll(PandorasBox.logs, PandorasBox.leaves);
        misc.addAll(PandorasBox.terracotta);
        flowers.addAll(PandorasBox.flowers);
        blocks.removeAll(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM, Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM);

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
            } else if (isBlockAnyOf(block, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE)) {
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
            } else if (isBlockAnyOf(block, Blocks.GRANITE, Blocks.TUFF)) {
                setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
            } else if (world.getBlockState(pos).isAir()) {
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
        if (random.nextDouble() < Math.pow(0.4, Math.floor(timesFeatureAMade / 8.0)))
        {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.isAir() && !blockBelowState.is(Blocks.NETHER_WART_BLOCK) && blockBelowState.isRedstoneConductor(world, posBelow))
            {
                Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                setBlockSafe(world, posBelow, Blocks.CRIMSON_NYLIUM.defaultBlockState());
                boolean success = Objects.requireNonNull(configuredFeatureRegistry.get(CRIMSON_FUNGUS_PLANTED)).place(world, world.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureAMade++;
            }
        }
        if (random.nextDouble() < Math.pow(0.6, Math.floor(timesFeatureBMade / 10.0)))
        {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.isAir() && !blockBelowState.is(Blocks.NETHER_WART_BLOCK) && blockBelowState.isRedstoneConductor(world, posBelow))
            {
                Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                setBlockSafe(world, posBelow, Blocks.CRIMSON_NYLIUM.defaultBlockState());
                boolean success = Objects.requireNonNull(configuredFeatureRegistry.get(CRIMSON_FOREST_VEGETATION)).place(world, world.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureBMade++;
            }
        }
        changeBiome(Biomes.CRIMSON_FOREST, pass, effectCenter, world);
    }
    public void createWarped(ServerLevel world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, int pass, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        ArrayListExtensions<Block> flowers = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.MOSS_BLOCK, Blocks.DIRT, Blocks.STONE, Blocks.ANDESITE, Blocks.GRANITE, Blocks.DEEPSLATE, Blocks.END_STONE, Blocks.MYCELIUM, Blocks.SAND, Blocks.SANDSTONE, Blocks.RED_SAND, Blocks.RED_SANDSTONE);
        blocks.addAll(PandorasBox.logs, PandorasBox.leaves);
        misc.addAll(PandorasBox.terracotta);
        flowers.addAll(PandorasBox.flowers);
        blocks.removeAll(Blocks.CRIMSON_STEM, Blocks.STRIPPED_CRIMSON_STEM, Blocks.WARPED_STEM, Blocks.STRIPPED_WARPED_STEM);

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
            } else if (isBlockAnyOf(block, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE)) {
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
            } else if (isBlockAnyOf(block, Blocks.GRANITE, Blocks.TUFF)) {
                setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
            } else if (world.getBlockState(pos).isAir()) {
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
        if (random.nextDouble() < Math.pow(0.4, Math.floor(timesFeatureAMade / 8.0)))
        {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.isAir() && !blockBelowState.is(Blocks.NETHER_WART_BLOCK) && blockBelowState.isRedstoneConductor(world, posBelow))
            {
                Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                setBlockSafe(world, posBelow, Blocks.WARPED_NYLIUM.defaultBlockState());
                boolean success = Objects.requireNonNull(configuredFeatureRegistry.get(WARPED_FUNGUS_PLANTED)).place(world, world.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureAMade++;
            }
        }
        if (random.nextDouble() < Math.pow(0.6, Math.floor(timesFeatureBMade / 10.0)))
        {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.isAir() && !blockBelowState.is(Blocks.WARPED_WART_BLOCK) && blockBelowState.isRedstoneConductor(world, posBelow))
            {
                Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                setBlockSafe(world, posBelow, Blocks.WARPED_NYLIUM.defaultBlockState());
                boolean success = Objects.requireNonNull(configuredFeatureRegistry.get(WARPED_FOREST_VEGETION)).place(world, world.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureBMade++;
            }
        }
        changeBiome(Biomes.WARPED_FOREST, pass, effectCenter, world);
    }
    public void createDeltas(ServerLevel world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, int pass, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        blocks.addAll(Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
        misc.addAll(Blocks.GRASS_BLOCK, Blocks.MOSS_BLOCK, Blocks.DIRT, Blocks.END_STONE, Blocks.MYCELIUM);
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
            } else if (isBlockAnyOf(block, Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE)) {
                setBlockSafe(world, pos, Blocks.NETHER_GOLD_ORE.defaultBlockState());
            } else if (isBlockAnyOf(block, misc)) {
                setBlockSafe(world, pos, Blocks.BASALT.defaultBlockState());
            }  else if (isBlockAnyOf(block, Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.DEEPSLATE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE)) {
                setBlockSafe(world, pos, Blocks.NETHERRACK.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.GRANITE, Blocks.TUFF, Blocks.SAND, Blocks.RED_SAND)) {
                setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
            } else if (world.getBlockState(pos).isAir()) {
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
        if (random.nextDouble() < Math.pow(0.8, Math.floor(timesFeatureAMade / 4.0)))
        {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.isAir() && blockBelowState.isRedstoneConductor(world, posBelow))
            {
                Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                boolean success = Objects.requireNonNull(configuredFeatureRegistry.get(SMALL_BASALT_COLUMNS)).place(world, world.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureAMade++;
            }
        }
        if(random.nextDouble() < Math.pow(0.3, Math.floor(timesFeatureBMade / 4.0))) {
            BlockPos posBelow = pos.below();
            BlockState blockBelowState = world.getBlockState(posBelow);

            if (blockState.isAir() && blockBelowState.isRedstoneConductor(world, posBelow))
            {
                Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                boolean success = Objects.requireNonNull(configuredFeatureRegistry.get(LARGE_BASALT_COLUMNS)).place(world, world.getChunkSource().getGenerator(), random, pos);
                if(success) timesFeatureBMade++;
            }
        }
        changeBiome(Biomes.BASALT_DELTAS, pass, effectCenter, world);
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
