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
public record PBECConvertToOverworld(DValue range) implements PBEffectCreator {
    public static final List<BlockMapper> OVERWORLD_MAPPERS;
    public static final List<EntitySpawner> OVERWORLD_SPAWNERS;
    public static final List<FeatureGenerator> OVERWORLD_FEATURES;
    static {
        OVERWORLD_MAPPERS = new ArrayList<>();
        OVERWORLD_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.SNOW), Either.right(BlockTags.FIRE), Either.left(Blocks.BROWN_MUSHROOM), Either.left(Blocks.RED_MUSHROOM),
                Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM_BLOCK), Either.left(Blocks.MUSHROOM_STEM)}, Blocks.AIR));
        OVERWORLD_MAPPERS.add(new SurfaceMapper(new Either[] {Either.right(PandorasBox.ALL_TERRACOTTA),
                Either.right(ConventionalBlockTags.STONES), Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(BlockTags.BASE_STONE_NETHER),
                Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(BlockTags.NYLIUM), Either.right(BlockTags.DIRT),
                Either.right(BlockTags.SAND), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE)}, Blocks.GRASS_BLOCK, Blocks.DIRT, 0));
        OVERWORLD_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.right(ConventionalBlockTags.OBSIDIANS), Either.left(Blocks.LAVA), Either.left(Blocks.ICE)}, Blocks.WATER));
        OVERWORLD_SPAWNERS = new ArrayList<>();
        OVERWORLD_SPAWNERS.add(new SpawnRandom("pig", 1.0f / (30 * 30)));
        OVERWORLD_SPAWNERS.add(new SpawnRandom("cow", 1.0f / (30 * 30)));
        OVERWORLD_SPAWNERS.add(new SpawnRandom("sheep", 1.0f / (30 * 30)));
        OVERWORLD_SPAWNERS.add(new SpawnRandom("chicken", 1.0f / (30 * 30)));
        OVERWORLD_FEATURES = new ArrayList<>();
        OVERWORLD_FEATURES.add(new GenerateGeneric(0.05, new Either[] {Either.left(Blocks.GRASS_BLOCK)}, new ResourceKey[] {VegetationFeatures.PATCH_GRASS, VegetationFeatures.PATCH_SUNFLOWER, VegetationFeatures.TREES_PLAINS, VegetationFeatures.FLOWER_PLAIN}, Blocks.GRASS_BLOCK, true));
    }
    public static final MapCodec<PBECConvertToOverworld> CODEC = DValue.CODEC.fieldOf("range").xmap(PBECConvertToOverworld::new, PBECConvertToOverworld::range);

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        return new PBEffectGenerate(time, range, 3, PandorasBoxHelper.getRandomUnifiedSeed(random), new SimpleConvertEffect(Optional.of(Biomes.SUNFLOWER_PLAINS), OVERWORLD_MAPPERS, OVERWORLD_FEATURES, OVERWORLD_SPAWNERS));
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
