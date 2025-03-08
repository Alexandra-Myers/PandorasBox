/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenerate;
import ivorius.pandorasbox.effects.generate.SimpleConvertEffect;
import ivorius.pandorasbox.effects.generate.block_mappers.BlockMapper;
import ivorius.pandorasbox.effects.generate.block_mappers.SimpleConvertMapper;
import ivorius.pandorasbox.effects.generate.block_mappers.SurfaceMapper;
import ivorius.pandorasbox.effects.generate.entity_spawners.EntitySpawner;
import ivorius.pandorasbox.effects.generate.entity_spawners.SpawnRandom;
import ivorius.pandorasbox.effects.generate.feature_generators.FeatureGenerator;
import ivorius.pandorasbox.effects.generate.feature_generators.GenerateGeneric;
import ivorius.pandorasbox.random.DValue;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECConvertToMushroom(DValue range) implements PBEffectCreator {
    public static final List<BlockMapper> MUSHROOM_MAPPERS;
    public static final List<EntitySpawner> MUSHROOM_SPAWNERS;
    public static final List<FeatureGenerator> MUSHROOM_FEATURES;
    static {
        MUSHROOM_MAPPERS = new ArrayList<>();
        MUSHROOM_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.LOGS), Either.right(BlockTags.LEAVES), Either.right(BlockTags.FLOWERS),
                Either.right(BlockTags.SNOW), Either.right(BlockTags.FIRE), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN),
                Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SEAGRASS), Either.left(Blocks.TALL_SEAGRASS)}, Blocks.AIR));
        MUSHROOM_MAPPERS.add(new SurfaceMapper(new Either[] {Either.right(PandorasBox.ALL_TERRACOTTA), Either.right(ConventionalBlockTags.STONES), Either.right(ConventionalBlockTags.COBBLESTONES),
                Either.right(BlockTags.BASE_STONE_NETHER), Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(BlockTags.NYLIUM),
                Either.right(BlockTags.DIRT), Either.right(BlockTags.SAND), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS),
                Either.left(Blocks.END_STONE)}, Blocks.MYCELIUM, Blocks.DIRT, 0));
        MUSHROOM_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.right(ConventionalBlockTags.OBSIDIANS), Either.left(Blocks.LAVA), Either.left(Blocks.ICE)}, Blocks.WATER));
        MUSHROOM_SPAWNERS = new ArrayList<>();
        MUSHROOM_SPAWNERS.add(new SpawnRandom("mooshroom", 1.0f / (20 * 20)));
        MUSHROOM_FEATURES = new ArrayList<>();
        MUSHROOM_FEATURES.add(new GenerateGeneric(0.03, new Either[] {Either.right(ConventionalBlockTags.STONES), Either.left(Blocks.RED_MUSHROOM_BLOCK), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.MUSHROOM_STEM)}, new ResourceKey[] {TreeFeatures.HUGE_BROWN_MUSHROOM, TreeFeatures.HUGE_RED_MUSHROOM}, Blocks.MYCELIUM, false));
        MUSHROOM_FEATURES.add(new GenerateGeneric(0.04, new Either[] {Either.right(ConventionalBlockTags.STONES), Either.left(Blocks.RED_MUSHROOM_BLOCK), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.MUSHROOM_STEM)}, new ResourceKey[] {VegetationFeatures.PATCH_RED_MUSHROOM, VegetationFeatures.PATCH_BROWN_MUSHROOM, VegetationFeatures.MUSHROOM_ISLAND_VEGETATION}, Blocks.MYCELIUM, false));
    }
    public static final MapCodec<PBECConvertToMushroom> CODEC = DValue.CODEC.fieldOf("range").xmap(PBECConvertToMushroom::new, PBECConvertToMushroom::range);

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        return new PBEffectGenerate(time, range, 3, PandorasBoxHelper.getRandomUnifiedSeed(random), new SimpleConvertEffect(Optional.of(Biomes.MUSHROOM_FIELDS), MUSHROOM_MAPPERS, MUSHROOM_FEATURES, MUSHROOM_SPAWNERS));
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return 0.1f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
