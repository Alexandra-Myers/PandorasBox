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
import ivorius.pandorasbox.effects.generate.entity_spawners.EntitySpawner;
import ivorius.pandorasbox.effects.generate.entity_spawners.SpawnRandom;
import ivorius.pandorasbox.effects.generate.feature_generators.FeatureGenerator;
import ivorius.pandorasbox.effects.generate.feature_generators.GenerateGeneric;
import ivorius.pandorasbox.random.DValue;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.data.worldgen.features.VegetationFeatures;
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
public record PBECConvertToDesert(DValue range) implements PBEffectCreator {
    public static final List<BlockMapper> DESERT_MAPPERS;
    public static final List<EntitySpawner> DESERT_SPAWNERS;
    public static final List<FeatureGenerator> DESERT_FEATURES;
    static {
        DESERT_MAPPERS = new ArrayList<>();
        DESERT_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.LEAVES), Either.right(BlockTags.FLOWERS), Either.right(BlockTags.SNOW), Either.left(Blocks.ICE), Either.left(Blocks.WATER), Either.left(Blocks.VINE), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN), Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SEAGRASS), Either.left(Blocks.TALL_SEAGRASS), Either.left(Blocks.BROWN_MUSHROOM), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM), Either.left(Blocks.RED_MUSHROOM_BLOCK), Either.left(Blocks.MUSHROOM_STEM), Either.right(BlockTags.LOGS)}, Blocks.AIR));
        DESERT_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.SAND), Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(BlockTags.NYLIUM), Either.right(BlockTags.DIRT), Either.left(Blocks.NETHERRACK)}, Blocks.SAND));
        DESERT_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.right(PandorasBox.ALL_TERRACOTTA), Either.right(ConventionalBlockTags.STONES), Either.right(BlockTags.BASE_STONE_NETHER), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE)}, Blocks.SANDSTONE));
        DESERT_SPAWNERS = new ArrayList<>();
        DESERT_SPAWNERS.add(new SpawnRandom("camel", 1.0f / (20 * 20)));
        DESERT_FEATURES = new ArrayList<>();
        DESERT_FEATURES.add(new GenerateGeneric(0.03, new Either[] {Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS)}, VegetationFeatures.PATCH_CACTUS, Blocks.SAND));
        DESERT_FEATURES.add(new GenerateGeneric(0.05, new Either[] {Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS)}, VegetationFeatures.PATCH_DEAD_BUSH, Blocks.SAND));
    }
    public static final MapCodec<PBECConvertToDesert> CODEC = DValue.CODEC.fieldOf("range").xmap(PBECConvertToDesert::new, PBECConvertToDesert::range);

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        return new PBEffectGenerate(time, range, 3, PandorasBoxHelper.getRandomUnifiedSeed(random), new SimpleConvertEffect(Optional.of(Biomes.DESERT), DESERT_MAPPERS, DESERT_FEATURES, DESERT_SPAWNERS));
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
