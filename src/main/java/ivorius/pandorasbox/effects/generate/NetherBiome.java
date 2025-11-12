package ivorius.pandorasbox.effects.generate;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.PandoraBlockTags;
import net.atlas.atlascore.util.ArrayListExtensions;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

import static ivorius.pandorasbox.effects.PBEffect.*;
import static net.minecraft.data.worldgen.features.NetherFeatures.*;
import static net.minecraft.data.worldgen.features.TreeFeatures.CRIMSON_FUNGUS_PLANTED;
import static net.minecraft.data.worldgen.features.TreeFeatures.WARPED_FUNGUS_PLANTED;

public enum NetherBiome implements StringRepresentable {
    NETHER_WASTES("nether_wastes", Biomes.NETHER_WASTES) {
        @Override
        public void create(ServerLevel world, PandorasBoxEntity entity, RandomSource random, int pass, float newRatio, BlockPos pos, double discardNetherrackChance) {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0) {
                if (random.nextDouble() < (discardNetherrackChance / 100) * Math.pow(1 + (discardNetherrackChance * 2), newRatio * 100)) {
                    return;
                } else if (isBlockAnyOf(block, Either.left(Blocks.GRANITE), Either.left(Blocks.ANDESITE), Either.left(Blocks.TUFF))) {
                    setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(PandoraBlockTags.OBSIDIANS), Either.left(Blocks.ICE), Either.left(Blocks.WATER))) {
                    Optional<Integer> integer = blockState.getOptionalValue(LiquidBlock.LEVEL);
                    BlockState blockState2 = Blocks.LAVA.defaultBlockState();
                    if (integer.isPresent()) {
                        blockState2 = blockState2.setValue(LiquidBlock.LEVEL, integer.get());
                    }
                    setBlockSafe(world, pos, blockState2);
                } else if (isBlockAnyOf(block, Either.right(BlockTags.FLOWERS), Either.right(BlockTags.LOGS), Either.right(BlockTags.LEAVES), Either.right(BlockTags.SNOW), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN), Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SEAGRASS), Either.left(Blocks.TALL_SEAGRASS), Either.left(Blocks.DEAD_BUSH), Either.left(Blocks.VINE), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM_BLOCK))) {
                    setBlockToAirSafe(world, pos);
                } else if (isBlockAnyOf(block, Either.left(Blocks.CLAY))) {
                    setBlockSafe(world, pos, Blocks.MAGMA_BLOCK.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(BlockTags.COAL_ORES))) {
                    setBlockSafe(world, pos, Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(BlockTags.GOLD_ORES))) {
                    setBlockSafe(world, pos, Blocks.NETHER_GOLD_ORE.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(BlockTags.SAND))) {
                    setBlockSafe(world, pos, Blocks.SOUL_SAND.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(PandoraBlockTags.ALL_TERRACOTTA), Either.right(ConventionalBlockTags.STONES), Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(BlockTags.DIRT), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE))) {
                    setBlockSafe(world, pos, Blocks.NETHERRACK.defaultBlockState());
                } else if (world.getBlockState(pos).isAir()) {
                    if (random.nextInt(25) == 0) {
                        if (world.random.nextFloat() < 0.99f) {
                            if (world.getBlockState(pos.below()).is(BlockTags.SOUL_FIRE_BASE_BLOCKS))
                                setBlockSafe(world, pos, Blocks.SOUL_FIRE.defaultBlockState());
                            else if (!world.getBlockState(pos.below()).isAir())
                                setBlockSafe(world, pos, Blocks.FIRE.defaultBlockState());
                        } else {
                            setBlockSafe(world, pos, Blocks.GLOWSTONE.defaultBlockState());
                        }
                    } else if(!world.getBlockState(pos.above()).isAir() && random.nextFloat() < 0.02) {
                        createGlowstoneBlobs(world, pos, random);
                    }
                }
            } else if (pass == 1) {
                if (random.nextDouble() < Math.pow(0.04, expFromRatio(newRatio))) {
                    BlockPos posBelow = pos.below();
                    BlockState blockBelowState = world.getBlockState(posBelow);

                    if (blockState.isAir() && blockBelowState.isRedstoneConductor(world, posBelow)) {
                        Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                        configuredFeatureRegistry.getOrThrow(VegetationFeatures.PATCH_BROWN_MUSHROOM).place(world, world.getChunkSource().getGenerator(), random, pos);
                    }
                }
                if (random.nextDouble() < Math.pow(0.03, expFromRatio(newRatio))) {
                    BlockPos posBelow = pos.below();
                    BlockState blockBelowState = world.getBlockState(posBelow);

                    if (blockState.isAir() && blockBelowState.isRedstoneConductor(world, posBelow)) {
                        Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                        Objects.requireNonNull(configuredFeatureRegistry.getOrThrow(VegetationFeatures.PATCH_RED_MUSHROOM)).place(world, world.getChunkSource().getGenerator(), random, pos);
                    }
                }
            } else {
                ArrayListExtensions<Entity[]> entities = new ArrayListExtensions<>();
                entities.addAll(
                        lazilyCreateEntities(world, entity, random, "piglin", 1.0f / (30 * 30), pos),
                        lazilyCreateEntities(world, entity, random, "zombified_piglin", 1.0f / (15 * 15), pos),
                        lazilyCreateEntities(world, entity, random, "magma_cube", 1.0f / (15 * 15), pos),
                        lazilyCreateEntities(world, entity, random, "hoglin", 1.0f / (20 * 20), pos));

                for (Entity[] entity1 : entities) {
                    canSpawnEntities(world, pos, entity1);
                }

                if (canSpawnFlyingEntity(world, blockState, pos)) {
                    lazilySpawnFlyingEntities(world, entity, random, "ghast", 1.0f / (50 * 50 * 50), pos);
                    lazilySpawnFlyingEntities(world, entity, random, "blaze", 1.0f / (50 * 50 * 50), pos);
                }
            }
        }
    },
    SOUL_SAND_VALLEY("soul_sand_valley", Biomes.SOUL_SAND_VALLEY) {
        @Override
        public void create(ServerLevel world, PandorasBoxEntity entity, RandomSource random, int pass, float newRatio, BlockPos pos, double discardNetherrackChance) {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0) {
                if (random.nextDouble() < (discardNetherrackChance / 100) * Math.pow(1 + (discardNetherrackChance * 2), newRatio * 100)) return;
                if (isBlockAnyOf(block, Either.left(Blocks.GRANITE), Either.left(Blocks.ANDESITE), Either.left(Blocks.TUFF))) {
                    setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(PandoraBlockTags.OBSIDIANS), Either.left(Blocks.ICE), Either.left(Blocks.WATER))) {
                    Optional<Integer> integer = blockState.getOptionalValue(LiquidBlock.LEVEL);
                    BlockState blockState2 = Blocks.LAVA.defaultBlockState();
                    if (integer.isPresent()) {
                        blockState2 = blockState2.setValue(LiquidBlock.LEVEL, integer.get());
                    }
                    setBlockSafe(world, pos, blockState2);
                } else if (isBlockAnyOf(block, Either.right(BlockTags.FLOWERS), Either.right(BlockTags.LOGS), Either.right(BlockTags.LEAVES), Either.right(BlockTags.SNOW), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN), Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SEAGRASS), Either.left(Blocks.TALL_SEAGRASS), Either.left(Blocks.DEAD_BUSH), Either.left(Blocks.VINE), Either.left(Blocks.BROWN_MUSHROOM), Either.left(Blocks.RED_MUSHROOM), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM_BLOCK))) {
                    setBlockToAirSafe(world, pos);
                } else if (isBlockAnyOf(block, Either.right(BlockTags.SAND), Either.left(Blocks.CLAY))) {
                    setBlockSafe(world, pos, Blocks.SOUL_SAND.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(BlockTags.COAL_ORES))) {
                    setBlockSafe(world, pos, Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(BlockTags.GOLD_ORES))) {
                    setBlockSafe(world, pos, Blocks.NETHER_GOLD_ORE.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(PandoraBlockTags.ALL_TERRACOTTA), Either.right(BlockTags.DIRT))) {
                    setBlockSafe(world, pos, Blocks.SOUL_SOIL.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(ConventionalBlockTags.STONES), Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE))) {
                    setBlockSafe(world, pos, Blocks.NETHERRACK.defaultBlockState());
                } else if (world.getBlockState(pos).isAir()) {
                    boolean bl = !isBlockAnyOf(world.getBlockState(pos.below()).getBlock(), Either.left(Blocks.BONE_BLOCK)) ? random.nextInt(40) == 0 : random.nextInt(20) == 0;
                    if (random.nextInt(25) == 0) {
                        if (world.random.nextFloat() < 0.99f) {
                            if(world.getBlockState(pos.below()).is(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
                                setBlockSafe(world, pos, Blocks.SOUL_FIRE.defaultBlockState());
                            } else if(!world.getBlockState(pos.below()).isAir())
                                setBlockSafe(world, pos, Blocks.FIRE.defaultBlockState());
                        } else {
                            setBlockSafe(world, pos, Blocks.GLOWSTONE.defaultBlockState());
                        }
                    } else if (!world.getBlockState(pos.above()).isAir() && random.nextFloat() < 0.02) {
                        createGlowstoneBlobs(world, pos, random);
                    } else if (!world.getBlockState(pos.below()).isAir() && !isBlockAnyOf(world.getBlockState(pos.below()).getBlock(), Either.left(Blocks.GLOWSTONE)) && bl) {
                        setBlockSafe(world, pos, Blocks.BONE_BLOCK.defaultBlockState());
                    }
                }
            } else {
                ArrayListExtensions<Entity[]> entities = new ArrayListExtensions<>();
                entities.addAll(
                        lazilyCreateEntities(world, entity, random, "skeleton", 1.0f / (15 * 15), pos),
                        lazilyCreateEntities(world, entity, random, "zombified_piglin", 1.0f / (15 * 15), pos));

                for (Entity[] entity1 : entities) {
                    canSpawnEntities(world, pos, entity1);
                }

                if (canSpawnFlyingEntity(world, blockState, pos)) {
                    lazilySpawnFlyingEntities(world, entity, random, "ghast", 1.0f / (50 * 50 * 50), pos);
                    lazilySpawnFlyingEntities(world, entity, random, "blaze", 1.0f / (50 * 50 * 50), pos);
                }
            }
//TODO - Somehow make this performance not atrocious?
//                if (random.nextDouble() < Math.pow(0.05, expFromRatio(newRatio))) {
//                    ChunkGenerator chunkGenerator = world.getChunkSource().getGenerator();
//                    Registry<Structure> structureRegistry = world.registryAccess().registryOrThrow(Registries.STRUCTURE);
//                    StructureStart start = structureRegistry.getOrThrow(BuiltinStructures.NETHER_FOSSIL).generate(structureRegistry.getOrThrow(BuiltinStructures.NETHER_FOSSIL),
//                            Level.NETHER,
//                            world.registryAccess(),
//                            chunkGenerator,
//                            chunkGenerator.getBiomeSource(),
//                            world.getChunkSource().randomState(),
//                            world.getStructureManager(),
//                            world.getSeed(),
//                            new ChunkPos(pos),
//                            0,
//                            world,
//                            biomeHolder -> true);
//                    if(!start.isValid()) return;
//                    BoundingBox bounding box = start.getBoundingBox();
//                    ChunkPos chunkpos = new ChunkPos(SectionPos.blockToSectionCoord(boundingbox.minX()), SectionPos.blockToSectionCoord(boundingbox.minZ()));
//                    ChunkPos chunkpos1 = new ChunkPos(SectionPos.blockToSectionCoord(boundingbox.maxX()), SectionPos.blockToSectionCoord(boundingbox.maxZ()));
//                    ChunkPos.rangeClosed(chunkpos, chunkpos1).forEach((p_289290_) -> start.placeInChunk(world, world.structureManager(), world.getChunkSource().getGenerator(), world.getRandom(), new BoundingBox(p_289290_.getMinBlockX(), world.getMinY(), p_289290_.getMinBlockZ(), p_289290_.getMaxBlockX(), world.getMaxY(), p_289290_.getMaxBlockZ()), p_289290_));
//                }
        }
    },
    BASALT_DELTAS("basalt_deltas", Biomes.BASALT_DELTAS) {
        @Override
        public void create(ServerLevel world, PandorasBoxEntity entity, RandomSource random, int pass, float newRatio, BlockPos pos, double discardNetherrackChance) {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0) {
                if (random.nextDouble() < (discardNetherrackChance / 100) * Math.pow(1 + (discardNetherrackChance * 2), newRatio * 100)) {
                    return;
                } else if (isBlockAnyOf(block, Either.left(Blocks.GRANITE), Either.left(Blocks.ANDESITE), Either.left(Blocks.TUFF), Either.right(BlockTags.SAND))) {
                    setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(PandoraBlockTags.OBSIDIANS), Either.left(Blocks.ICE), Either.left(Blocks.WATER))) {
                    Optional<Integer> integer = blockState.getOptionalValue(LiquidBlock.LEVEL);
                    BlockState blockState2 = Blocks.LAVA.defaultBlockState();
                    if (integer.isPresent()) {
                        blockState2 = blockState2.setValue(LiquidBlock.LEVEL, integer.get());
                    }
                    setBlockSafe(world, pos, blockState2);
                } else if (isBlockAnyOf(block, Either.right(BlockTags.FLOWERS), Either.right(BlockTags.LOGS), Either.right(BlockTags.LEAVES), Either.right(BlockTags.SNOW), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN), Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SEAGRASS), Either.left(Blocks.TALL_SEAGRASS), Either.left(Blocks.DEAD_BUSH), Either.left(Blocks.VINE), Either.left(Blocks.BROWN_MUSHROOM), Either.left(Blocks.RED_MUSHROOM), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM_BLOCK))) {
                    setBlockToAirSafe(world, pos);
                } else if (isBlockAnyOf(block, Either.left(Blocks.CLAY))) {
                    setBlockSafe(world, pos, Blocks.MAGMA_BLOCK.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(BlockTags.COAL_ORES))) {
                    setBlockSafe(world, pos, Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(BlockTags.GOLD_ORES))) {
                    setBlockSafe(world, pos, Blocks.NETHER_GOLD_ORE.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(PandoraBlockTags.ALL_TERRACOTTA), Either.right(BlockTags.DIRT))) {
                    setBlockSafe(world, pos, Blocks.BASALT.defaultBlockState());
                }  else if (isBlockAnyOf(block, Either.right(ConventionalBlockTags.STONES), Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE))) {
                    setBlockSafe(world, pos, Blocks.NETHERRACK.defaultBlockState());
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
            } else if (pass == 1) {
                if (random.nextDouble() < Math.pow(0.06, expFromRatio(newRatio))) {
                    BlockPos posBelow = pos.below();
                    BlockState blockBelowState = world.getBlockState(posBelow);

                    if (blockState.isAir() && blockBelowState.isRedstoneConductor(world, posBelow)) {
                        Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                        configuredFeatureRegistry.getOrThrow(SMALL_BASALT_COLUMNS).place(world, world.getChunkSource().getGenerator(), random, pos);
                    }
                }
                if (random.nextDouble() < Math.pow(0.03, expFromRatio(newRatio))) {
                    BlockPos posBelow = pos.below();
                    BlockState blockBelowState = world.getBlockState(posBelow);

                    if (blockState.isAir() && blockBelowState.isRedstoneConductor(world, posBelow)) {
                        Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                        configuredFeatureRegistry.getOrThrow(LARGE_BASALT_COLUMNS).place(world, world.getChunkSource().getGenerator(), random, pos);
                    }
                }
            } else {
                ArrayListExtensions<Entity[]> entities = new ArrayListExtensions<>();
                entities.addAll(
                        lazilyCreateEntities(world, entity, random, "magma_cube", 1.0f / (15 * 15), pos));

                for (Entity[] entity1 : entities) {
                    canSpawnEntities(world, pos, entity1);
                }

                if (canSpawnFlyingEntity(world, blockState, pos)) {
                    lazilySpawnFlyingEntities(world, entity, random, "ghast", 1.0f / (50 * 50 * 50), pos);
                    lazilySpawnFlyingEntities(world, entity, random, "blaze", 1.0f / (50 * 50 * 50), pos);
                }
            }
        }
    },
    CRIMSON_FOREST("crimson_forest", Biomes.CRIMSON_FOREST) {
        @Override
        public void create(ServerLevel world, PandorasBoxEntity entity, RandomSource random, int pass, float newRatio, BlockPos pos, double discardNetherrackChance) {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0) {
                if (random.nextDouble() < (discardNetherrackChance / 100) * Math.pow(1 + (discardNetherrackChance * 2), newRatio * 100)) {
                    return;
                } else if (isBlockAnyOf(block, Either.left(Blocks.GRANITE), Either.left(Blocks.ANDESITE), Either.left(Blocks.TUFF))) {
                    setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(PandoraBlockTags.OBSIDIANS), Either.left(Blocks.ICE), Either.left(Blocks.WATER))) {
                    Optional<Integer> integer = blockState.getOptionalValue(LiquidBlock.LEVEL);
                    BlockState blockState2 = Blocks.LAVA.defaultBlockState();
                    if (integer.isPresent()) {
                        blockState2 = blockState2.setValue(LiquidBlock.LEVEL, integer.get());
                    }
                    setBlockSafe(world, pos, blockState2);
                } else if (isBlockAnyOf(block, Either.right(BlockTags.LOGS_THAT_BURN), Either.right(BlockTags.LEAVES), Either.right(BlockTags.SNOW), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN), Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SEAGRASS), Either.left(Blocks.TALL_SEAGRASS), Either.left(Blocks.DEAD_BUSH), Either.left(Blocks.VINE), Either.left(Blocks.BROWN_MUSHROOM), Either.left(Blocks.RED_MUSHROOM), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM_BLOCK))) {
                    setBlockToAirSafe(world, pos);
                } else if (isBlockAnyOf(block, Either.left(Blocks.CLAY))) {
                    setBlockSafe(world, pos, Blocks.MAGMA_BLOCK.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(BlockTags.COAL_ORES))) {
                    setBlockSafe(world, pos, Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(BlockTags.GOLD_ORES))) {
                    setBlockSafe(world, pos, Blocks.NETHER_GOLD_ORE.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(PandoraBlockTags.ALL_TERRACOTTA), Either.right(BlockTags.SAND), Either.right(ConventionalBlockTags.STONES), Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(BlockTags.DIRT), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE))) {
                    if (random.nextDouble() < 0.2 || !world.getBlockState(pos.above()).isAir())
                        setBlockSafe(world, pos, Blocks.NETHERRACK.defaultBlockState());
                    else
                        setBlockSafe(world, pos, Blocks.CRIMSON_NYLIUM.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(BlockTags.FLOWERS))) {
                    if (random.nextDouble() < 0.5)
                        setBlockSafe(world, pos, Blocks.CRIMSON_ROOTS.defaultBlockState());
                    else
                        setBlockSafe(world, pos, Blocks.CRIMSON_FUNGUS.defaultBlockState());
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
            } else if (pass == 1) {
                if (random.nextDouble() < Math.pow(0.04, expFromRatio(newRatio))) {
                    BlockPos posBelow = pos.below();
                    BlockState blockBelowState = world.getBlockState(posBelow);

                    if (blockState.isAir() && !blockBelowState.is(Blocks.NETHER_WART_BLOCK) && blockBelowState.isRedstoneConductor(world, posBelow)) {
                        Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                        setBlockSafe(world, posBelow, Blocks.CRIMSON_NYLIUM.defaultBlockState());
                        configuredFeatureRegistry.getOrThrow(CRIMSON_FUNGUS_PLANTED).place(world, world.getChunkSource().getGenerator(), random, pos);
                    }
                }
                if (random.nextDouble() < Math.pow(0.05, expFromRatio(newRatio))) {
                    BlockPos posBelow = pos.below();
                    BlockState blockBelowState = world.getBlockState(posBelow);

                    if (blockState.isAir() && !blockBelowState.is(Blocks.NETHER_WART_BLOCK) && blockBelowState.isRedstoneConductor(world, posBelow)) {
                        Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                        setBlockSafe(world, posBelow, Blocks.CRIMSON_NYLIUM.defaultBlockState());
                        configuredFeatureRegistry.getOrThrow(CRIMSON_FOREST_VEGETATION).place(world, world.getChunkSource().getGenerator(), random, pos);
                    }
                }
            } else {
                ArrayListExtensions<Entity[]> entities = new ArrayListExtensions<>();
                entities.addAll(
                        lazilyCreateEntities(world, entity, random, "piglin", 1.0f / (10 * 10), pos),
                        lazilyCreateEntities(world, entity, random, "zombified_piglin", 1.0f / (25 * 25), pos),
                        lazilyCreateEntities(world, entity, random, "hoglin", 1.0f / (10 * 10), pos));

                for (Entity[] entity1 : entities) {
                    canSpawnEntities(world, pos, entity1);
                }

                if (canSpawnFlyingEntity(world, blockState, pos)) {
                    lazilySpawnFlyingEntities(world, entity, random, "ghast", 1.0f / (50 * 50 * 50), pos);
                    lazilySpawnFlyingEntities(world, entity, random, "blaze", 1.0f / (50 * 50 * 50), pos);
                }
            }
        }
    },
    WARPED_FOREST("warped_forest", Biomes.WARPED_FOREST) {
        @Override
        public void create(ServerLevel world, PandorasBoxEntity entity, RandomSource random, int pass, float newRatio, BlockPos pos, double discardNetherrackChance) {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0) {
                if (random.nextDouble() < (discardNetherrackChance / 100) * Math.pow(1 + (discardNetherrackChance * 2), newRatio * 100)) {
                    return;
                } else if (isBlockAnyOf(block, Either.left(Blocks.GRANITE), Either.left(Blocks.ANDESITE), Either.left(Blocks.TUFF))) {
                    setBlockSafe(world, pos, Blocks.BLACKSTONE.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(PandoraBlockTags.OBSIDIANS), Either.left(Blocks.ICE), Either.left(Blocks.WATER))) {
                    Optional<Integer> integer = blockState.getOptionalValue(LiquidBlock.LEVEL);
                    BlockState blockState2 = Blocks.LAVA.defaultBlockState();
                    if (integer.isPresent()) {
                        blockState2 = blockState2.setValue(LiquidBlock.LEVEL, integer.get());
                    }
                    setBlockSafe(world, pos, blockState2);
                } else if (isBlockAnyOf(block, Either.right(BlockTags.LOGS_THAT_BURN), Either.right(BlockTags.LEAVES), Either.right(BlockTags.SNOW), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN), Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SEAGRASS), Either.left(Blocks.TALL_SEAGRASS), Either.left(Blocks.DEAD_BUSH), Either.left(Blocks.VINE), Either.left(Blocks.BROWN_MUSHROOM), Either.left(Blocks.RED_MUSHROOM), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM_BLOCK))) {
                    setBlockToAirSafe(world, pos);
                } else if (isBlockAnyOf(block, Either.left(Blocks.CLAY))) {
                    setBlockSafe(world, pos, Blocks.MAGMA_BLOCK.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(BlockTags.COAL_ORES))) {
                    setBlockSafe(world, pos, Blocks.NETHER_QUARTZ_ORE.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(BlockTags.GOLD_ORES))) {
                    setBlockSafe(world, pos, Blocks.NETHER_GOLD_ORE.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(PandoraBlockTags.ALL_TERRACOTTA), Either.right(BlockTags.SAND), Either.right(ConventionalBlockTags.STONES), Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(BlockTags.DIRT), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE))) {
                    if (random.nextDouble() < 0.2 || !world.getBlockState(pos.above()).isAir())
                        setBlockSafe(world, pos, Blocks.NETHERRACK.defaultBlockState());
                    else
                        setBlockSafe(world, pos, Blocks.WARPED_NYLIUM.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(BlockTags.FLOWERS))) {
                    if (random.nextDouble() < 0.5)
                        setBlockSafe(world, pos, Blocks.WARPED_ROOTS.defaultBlockState());
                    else
                        setBlockSafe(world, pos, Blocks.WARPED_FUNGUS.defaultBlockState());
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
            } else if (pass == 1) {
                if (random.nextDouble() < Math.pow(0.04, expFromRatio(newRatio))) {
                    BlockPos posBelow = pos.below();
                    BlockState blockBelowState = world.getBlockState(posBelow);

                    if (blockState.isAir() && !blockBelowState.is(Blocks.WARPED_WART_BLOCK) && blockBelowState.isRedstoneConductor(world, posBelow)) {
                        Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                        setBlockSafe(world, posBelow, Blocks.WARPED_NYLIUM.defaultBlockState());
                        configuredFeatureRegistry.getOrThrow(WARPED_FUNGUS_PLANTED).place(world, world.getChunkSource().getGenerator(), random, pos);
                    }
                }
                if (random.nextDouble() < Math.pow(0.05, expFromRatio(newRatio))) {
                    BlockPos posBelow = pos.below();
                    BlockState blockBelowState = world.getBlockState(posBelow);

                    if (blockState.isAir() && !blockBelowState.is(Blocks.WARPED_WART_BLOCK) && blockBelowState.isRedstoneConductor(world, posBelow)) {
                        Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
                        setBlockSafe(world, posBelow, Blocks.WARPED_NYLIUM.defaultBlockState());
                        configuredFeatureRegistry.getOrThrow(WARPED_FOREST_VEGETION).place(world, world.getChunkSource().getGenerator(), random, pos);
                    }
                }
            } else {
                ArrayListExtensions<Entity[]> entities = new ArrayListExtensions<>();
                entities.addAll(
                        lazilyCreateEntities(world, entity, random, "enderman", 1.0f / (15 * 15), pos));

                for (Entity[] entity1 : entities) {
                    canSpawnEntities(world, pos, entity1);
                }
            }
        }
    };
        
    public static void createGlowstoneBlobs(ServerLevel world, BlockPos pos, RandomSource random) {
        Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
        configuredFeatureRegistry.getOrThrow(GLOWSTONE_EXTRA).place(world, world.getChunkSource().getGenerator(), random, pos);
    }
    public static final Codec<NetherBiome> CODEC = StringRepresentable.fromEnum(NetherBiome::values);
    public final String name;
    public final ResourceKey<Biome> biomeResourceKey;

    NetherBiome(String name, ResourceKey<Biome> biomeResourceKey) {
        this.name = name;
        this.biomeResourceKey = biomeResourceKey;
    }

    public static double expFromRatio(double newRatio) {
        return newRatio + 1;
    }
    
    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
        
    public abstract void create(ServerLevel world, PandorasBoxEntity entity, RandomSource random, int pass, float newRatio, BlockPos pos, double discardNetherrackChance);
}