package ivorius.pandorasbox.effectcreators;

import com.mojang.datafixers.util.Either;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.effects.generate.SimpleConvertEffect;
import ivorius.pandorasbox.effects.generate.block_mappers.*;
import ivorius.pandorasbox.effects.generate.entity_spawners.EntitySpawner;
import ivorius.pandorasbox.effects.generate.entity_spawners.SpawnRandom;
import ivorius.pandorasbox.effects.generate.feature_generators.FeatureGenerator;
import ivorius.pandorasbox.effects.generate.feature_generators.GenerateGeneric;
import ivorius.pandorasbox.effects.generate.feature_generators.GenerateHomo;
import ivorius.pandorasbox.weighted.WeightedResourceKey;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.data.worldgen.features.EndFeatures;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class PBECConversions {
    public static final SimpleConvertEffect CHRISTMAS;
    public static final SimpleConvertEffect DESERT;
    public static final SimpleConvertEffect END;
    public static final SimpleConvertEffect HALLOWEEN;
    public static final SimpleConvertEffect HEAVENLY;
    public static final SimpleConvertEffect HOMO;
    public static final SimpleConvertEffect ICE;
    public static final SimpleConvertEffect LIFELESS;
    public static final SimpleConvertEffect MUSHROOM;
    public static final SimpleConvertEffect OVERWORLD;

    static {
        List<BlockMapper> mappers = new ArrayList<>();
        mappers.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.WATER)}, Blocks.ICE));
        mappers.add(new ChrismasGiftsMapper());
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.FIRE)}, Blocks.AIR));
        mappers.add(new LavaChillMapper(Blocks.LAVA, Blocks.COBBLESTONE));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.MAGMA_BLOCK)}, Blocks.COBBLESTONE));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.LAVA)}, Blocks.OBSIDIAN));
        List<FeatureGenerator> generators = new ArrayList<>();
        List<EntitySpawner> spawners = new ArrayList<>();
        spawners.add(new SpawnRandom("pbspecial_hogfather", 1.0f / (150 * 150)));
        spawners.add(new SpawnRandom("snow_golem", 1.0f / (20 * 20)));
        CHRISTMAS = new SimpleConvertEffect(Optional.of(Biomes.SNOWY_TAIGA), mappers, Collections.emptyList(), spawners);

        mappers = new ArrayList<>();
        mappers.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.FERN)}, Blocks.SHORT_DRY_GRASS));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.TALL_GRASS), Either.left(Blocks.LARGE_FERN)}, Blocks.TALL_DRY_GRASS));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.LEAVES), Either.right(BlockTags.FLOWERS), Either.left(Blocks.VINE), Either.left(Blocks.BROWN_MUSHROOM), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM), Either.left(Blocks.RED_MUSHROOM_BLOCK), Either.left(Blocks.MUSHROOM_STEM), Either.right(BlockTags.LOGS)}, Blocks.AIR));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.SAND), Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(BlockTags.NYLIUM), Either.right(BlockTags.DIRT), Either.left(Blocks.NETHERRACK)}, Blocks.SAND));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(PandorasBox.ALL_TERRACOTTA), Either.right(ConventionalBlockTags.STONES), Either.right(BlockTags.BASE_STONE_NETHER), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE)}, Blocks.SANDSTONE));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.ICE)}, Blocks.WATER));
        spawners = new ArrayList<>();
        spawners.add(new SpawnRandom("camel", 1.0f / (20 * 20)));
        spawners.add(new SpawnRandom("pig", 1.0f / (30 * 30)));
        spawners.add(new SpawnRandom("cow", 1.0f / (30 * 30)));
        spawners.add(new SpawnRandom("sheep", 1.0f / (30 * 30)));
        spawners.add(new SpawnRandom("chicken", 1.0f / (30 * 30)));
        generators.add(new GenerateGeneric(0.03, new Either[] {Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS)}, VegetationFeatures.PATCH_CACTUS, Blocks.SAND));
        generators.add(new GenerateGeneric(0.05, new Either[] {Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS)}, WeightedResourceKey.ofKeysEqualWeight(VegetationFeatures.PATCH_DEAD_BUSH, VegetationFeatures.PATCH_DRY_GRASS), Blocks.SAND, false));
        DESERT = new SimpleConvertEffect(Optional.of(Biomes.DESERT), mappers, generators, spawners);

        mappers = new ArrayList<>();
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.LEAVES), Either.right(BlockTags.FLOWERS), Either.right(BlockTags.SNOW), Either.left(Blocks.ICE), Either.left(Blocks.WATER), Either.left(Blocks.VINE), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN), Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SHORT_DRY_GRASS), Either.left(Blocks.TALL_DRY_GRASS), Either.left(Blocks.DEAD_BUSH), Either.left(Blocks.BUSH), Either.left(Blocks.FIREFLY_BUSH), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM_BLOCK), Either.left(Blocks.MUSHROOM_STEM), Either.right(BlockTags.LOGS)}, Blocks.OBSIDIAN));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.BROWN_MUSHROOM), Either.left(Blocks.RED_MUSHROOM)}, Blocks.AIR));
        mappers.add(new SetAllSolid(Blocks.END_STONE));
        spawners = new ArrayList<>();
        spawners.add(new SpawnRandom("enderman", 1.0f / (20 * 20)));
        generators = new ArrayList<>();
        generators.add(new GenerateGeneric(0.02, new Either[] {Either.right(ConventionalBlockTags.OBSIDIANS), Either.left(Blocks.CHORUS_FLOWER), Either.left(Blocks.CHORUS_PLANT)}, EndFeatures.CHORUS_PLANT, Blocks.END_STONE));
        END = new SimpleConvertEffect(Optional.of(Biomes.END_BARRENS), mappers, generators, spawners);

        mappers = new ArrayList<>();
        mappers.add(new HalloweenMapper());
        spawners = new ArrayList<>();
        spawners.add(new SpawnRandom("zombified_piglin", 1.0f / (20 * 20)));
        spawners.add(new SpawnRandom("enderman", 1.0f / (20 * 20)));
        spawners.add(new SpawnRandom("phantom", 1.0f / (20 * 20)));
        HALLOWEEN = new SimpleConvertEffect(Optional.of(Biomes.SOUL_SAND_VALLEY), mappers, Collections.emptyList(), spawners);

        mappers = new ArrayList<>();
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.SNOW), Either.right(BlockTags.FIRE), Either.right(BlockTags.FLOWERS), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN), Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SHORT_DRY_GRASS), Either.left(Blocks.TALL_DRY_GRASS), Either.left(Blocks.DEAD_BUSH), Either.left(Blocks.BUSH), Either.left(Blocks.FIREFLY_BUSH)}, Blocks.AIR));
        mappers.add(new HeavenlyMapper(new Either[] {Either.right(PandorasBox.ALL_TERRACOTTA), Either.right(ConventionalBlockTags.STONES), Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(BlockTags.BASE_STONE_NETHER), Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(BlockTags.NYLIUM), Either.right(BlockTags.DIRT), Either.right(BlockTags.SAND), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE)}));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(ConventionalBlockTags.OBSIDIANS), Either.left(Blocks.LAVA), Either.left(Blocks.ICE)}, Blocks.WATER));
        spawners = new ArrayList<>();
        spawners.add(new SpawnRandom("sheep", 1.0f / (20 * 20)));
        HEAVENLY = new SimpleConvertEffect(Optional.of(Biomes.LUSH_CAVES), mappers, Collections.emptyList(), spawners);

        mappers = new ArrayList<>();
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.SNOW), Either.right(BlockTags.FIRE),
                Either.left(Blocks.SHORT_DRY_GRASS), Either.left(Blocks.TALL_DRY_GRASS), Either.left(Blocks.DEAD_BUSH),
                Either.left(Blocks.BROWN_MUSHROOM), Either.left(Blocks.RED_MUSHROOM),
                Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM_BLOCK), Either.left(Blocks.MUSHROOM_STEM)}, Blocks.AIR));
        mappers.add(new SurfaceMapper(new Either[] {Either.right(PandorasBox.ALL_TERRACOTTA), Either.right(ConventionalBlockTags.STONES),
                Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(BlockTags.BASE_STONE_NETHER),
                Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(BlockTags.NYLIUM), Either.right(BlockTags.DIRT),
                Either.right(BlockTags.SAND), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE)}, Blocks.GRASS_BLOCK, Blocks.DIRT, 0));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(ConventionalBlockTags.OBSIDIANS), Either.left(Blocks.LAVA), Either.left(Blocks.ICE)}, Blocks.WATER));
        spawners = new ArrayList<>();
        spawners.add(new SpawnRandom("pbspecial_colorful_sheep", 1.0f / (20 * 20)));
        generators = new ArrayList<>();
        generators.add(new GenerateHomo());
        HOMO = new SimpleConvertEffect(Optional.of(Biomes.FLOWER_FOREST), mappers, generators, spawners);

        mappers = new ArrayList<>();
        mappers.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.WATER)}, Blocks.ICE));
        mappers.add(new CoverMapper(Blocks.SNOW));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.FIRE)}, Blocks.AIR));
        mappers.add(new LavaChillMapper(Blocks.LAVA, Blocks.ICE));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.MAGMA_BLOCK)}, Blocks.ICE));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.LAVA)}, Blocks.PACKED_ICE));
        mappers.add(new IceMapper());
        spawners = new ArrayList<>();
        spawners.add(new SpawnRandom("snow_golem", 1.0f / (20 * 20)));
        ICE = new SimpleConvertEffect(Optional.of(Biomes.FROZEN_PEAKS), mappers, Collections.emptyList(), spawners);

        mappers = new ArrayList<>();
        mappers.add(new DryMapper());
        mappers.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.ICE), Either.left(Blocks.WATER), Either.left(Blocks.LAVA), Either.right(BlockTags.SNOW), Either.right(BlockTags.LOGS), Either.right(BlockTags.LEAVES), Either.left(Blocks.VINE), Either.left(Blocks.BROWN_MUSHROOM), Either.left(Blocks.RED_MUSHROOM), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM_BLOCK), Either.left(Blocks.MUSHROOM_STEM)}, Blocks.AIR));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.FLOWERS), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN), Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SEAGRASS), Either.left(Blocks.TALL_SEAGRASS), Either.left(Blocks.SHORT_DRY_GRASS), Either.left(Blocks.TALL_DRY_GRASS), Either.left(Blocks.DEAD_BUSH), Either.left(Blocks.BUSH), Either.left(Blocks.FIREFLY_BUSH)}, Blocks.DEAD_BUSH));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.NYLIUM), Either.right(BlockTags.DIRT), Either.right(BlockTags.WOOL), Either.left(Blocks.CAKE)}, Blocks.DIRT));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(PandorasBox.ALL_TERRACOTTA), Either.right(ConventionalBlockTags.STONES), Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(BlockTags.BASE_STONE_NETHER), Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE)}, Blocks.STONE));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(BlockTags.SAND)}, Blocks.SAND));
        LIFELESS = new SimpleConvertEffect(Optional.of(Biomes.BADLANDS), mappers, Collections.emptyList(), Collections.emptyList());

        mappers = new ArrayList<>();
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.LOGS), Either.right(BlockTags.LEAVES), Either.right(BlockTags.FLOWERS),
                Either.right(BlockTags.SNOW), Either.right(BlockTags.FIRE), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN),
                Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SHORT_DRY_GRASS), Either.left(Blocks.TALL_DRY_GRASS), Either.left(Blocks.DEAD_BUSH), Either.left(Blocks.BUSH), Either.left(Blocks.FIREFLY_BUSH)}, Blocks.AIR));
        mappers.add(new SurfaceMapper(new Either[] {Either.right(PandorasBox.ALL_TERRACOTTA), Either.right(ConventionalBlockTags.STONES), Either.right(ConventionalBlockTags.COBBLESTONES),
                Either.right(BlockTags.BASE_STONE_NETHER), Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(BlockTags.NYLIUM),
                Either.right(BlockTags.DIRT), Either.right(BlockTags.SAND), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS),
                Either.left(Blocks.END_STONE)}, Blocks.MYCELIUM, Blocks.DIRT, 0));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(ConventionalBlockTags.OBSIDIANS), Either.left(Blocks.LAVA), Either.left(Blocks.ICE)}, Blocks.WATER));
        spawners = new ArrayList<>();
        spawners.add(new SpawnRandom("mooshroom", 1.0f / (20 * 20)));
        generators = new ArrayList<>();
        generators.add(new GenerateGeneric(0.03, new Either[] {Either.right(ConventionalBlockTags.STONES), Either.left(Blocks.RED_MUSHROOM_BLOCK), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.MUSHROOM_STEM)}, WeightedResourceKey.ofKeysEqualWeight(TreeFeatures.HUGE_BROWN_MUSHROOM, TreeFeatures.HUGE_RED_MUSHROOM), Blocks.MYCELIUM, false));
        generators.add(new GenerateGeneric(0.04, new Either[] {Either.right(ConventionalBlockTags.STONES), Either.left(Blocks.RED_MUSHROOM_BLOCK), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.MUSHROOM_STEM)}, WeightedResourceKey.ofKeysEqualWeight(VegetationFeatures.PATCH_RED_MUSHROOM, VegetationFeatures.PATCH_BROWN_MUSHROOM, VegetationFeatures.MUSHROOM_ISLAND_VEGETATION), Blocks.MYCELIUM, false));
        MUSHROOM = new SimpleConvertEffect(Optional.of(Biomes.MUSHROOM_FIELDS), mappers, generators, spawners);

        mappers = new ArrayList<>();
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.SNOW), Either.right(BlockTags.FIRE), Either.left(Blocks.SHORT_DRY_GRASS), Either.left(Blocks.TALL_DRY_GRASS), Either.left(Blocks.DEAD_BUSH), Either.left(Blocks.BROWN_MUSHROOM), Either.left(Blocks.RED_MUSHROOM),
                Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM_BLOCK), Either.left(Blocks.MUSHROOM_STEM)}, Blocks.AIR));
        mappers.add(new SurfaceMapper(new Either[] {Either.right(PandorasBox.ALL_TERRACOTTA),
                Either.right(ConventionalBlockTags.STONES), Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(BlockTags.BASE_STONE_NETHER),
                Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(BlockTags.NYLIUM), Either.right(BlockTags.DIRT),
                Either.right(BlockTags.SAND), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE)}, Blocks.GRASS_BLOCK, Blocks.DIRT, 0));
        mappers.add(new SimpleConvertMapper(new Either[] {Either.right(ConventionalBlockTags.OBSIDIANS), Either.left(Blocks.LAVA), Either.left(Blocks.ICE)}, Blocks.WATER));
        spawners = new ArrayList<>();
        spawners.add(new SpawnRandom("pig", 1.0f / (30 * 30)));
        spawners.add(new SpawnRandom("cow", 1.0f / (30 * 30)));
        spawners.add(new SpawnRandom("sheep", 1.0f / (30 * 30)));
        spawners.add(new SpawnRandom("chicken", 1.0f / (30 * 30)));
        generators = new ArrayList<>();
        List<WeightedResourceKey<ConfiguredFeature<?, ?>>> features = new ArrayList<>(WeightedResourceKey.ofKeysEqualWeight(VegetationFeatures.PATCH_GRASS, VegetationFeatures.PATCH_TALL_GRASS, VegetationFeatures.PATCH_BUSH, VegetationFeatures.PATCH_FIREFLY_BUSH, VegetationFeatures.WILDFLOWERS_BIRCH_FOREST, VegetationFeatures.PATCH_SUNFLOWER, VegetationFeatures.FLOWER_PLAIN));
        features.add(new WeightedResourceKey<>(3.0, VegetationFeatures.TREES_PLAINS));
        generators.add(new GenerateGeneric(0.05, new Either[] {Either.left(Blocks.GRASS_BLOCK)}, features, Blocks.GRASS_BLOCK, true));
        OVERWORLD = new SimpleConvertEffect(Optional.of(Biomes.SUNFLOWER_PLAINS), mappers, generators, spawners);
    }
}
