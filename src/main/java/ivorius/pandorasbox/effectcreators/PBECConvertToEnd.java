/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenerate;
import ivorius.pandorasbox.effects.generate.SimpleConvertEffect;
import ivorius.pandorasbox.effects.generate.block_mappers.BlockMapper;
import ivorius.pandorasbox.effects.generate.block_mappers.SetAllSolid;
import ivorius.pandorasbox.effects.generate.block_mappers.SimpleConvertMapper;
import ivorius.pandorasbox.effects.generate.entity_spawners.EntitySpawner;
import ivorius.pandorasbox.effects.generate.entity_spawners.SpawnRandom;
import ivorius.pandorasbox.effects.generate.feature_generators.FeatureGenerator;
import ivorius.pandorasbox.effects.generate.feature_generators.GenerateGeneric;
import ivorius.pandorasbox.random.DValue;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.data.worldgen.features.EndFeatures;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECConvertToEnd(DValue range) implements PBEffectCreator {
    public static final Either<Block, TagKey<Block>>[] EXCLUDED_TARGETS = new Either[]{Either.right(ConventionalBlockTags.OBSIDIANS), Either.left(Blocks.CHORUS_PLANT), Either.left(Blocks.CHORUS_FLOWER)};
    public static final List<BlockMapper> END_MAPPERS;
    public static final List<EntitySpawner> END_SPAWNERS;
    public static final List<FeatureGenerator> END_FEATURES;
    static {
        END_MAPPERS = new ArrayList<>();
        END_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.LEAVES), Either.right(BlockTags.FLOWERS), Either.right(BlockTags.SNOW), Either.left(Blocks.ICE), Either.left(Blocks.WATER), Either.left(Blocks.VINE), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN), Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SEAGRASS), Either.left(Blocks.TALL_SEAGRASS), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM_BLOCK), Either.left(Blocks.MUSHROOM_STEM), Either.right(BlockTags.LOGS)}, Blocks.OBSIDIAN));
        END_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.BROWN_MUSHROOM), Either.left(Blocks.RED_MUSHROOM)}, Blocks.AIR));
        END_MAPPERS.add(new SetAllSolid(Blocks.END_STONE));
        END_SPAWNERS = new ArrayList<>();
        END_SPAWNERS.add(new SpawnRandom("enderman", 1.0f / (20 * 20)));
        END_FEATURES = new ArrayList<>();
        END_FEATURES.add(new GenerateGeneric(0.02, new Either[] {Either.right(ConventionalBlockTags.OBSIDIANS), Either.left(Blocks.CHORUS_FLOWER), Either.left(Blocks.CHORUS_PLANT)}, EndFeatures.CHORUS_PLANT, Blocks.END_STONE));
    }
    public static final MapCodec<PBECConvertToEnd> CODEC = DValue.CODEC.fieldOf("range").xmap(PBECConvertToEnd::new, PBECConvertToEnd::range);

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        return new PBEffectGenerate(time, range, 3, PandorasBoxHelper.getRandomUnifiedSeed(random), new SimpleConvertEffect(Optional.of(Biomes.END_BARRENS), EXCLUDED_TARGETS, END_MAPPERS, END_FEATURES, END_SPAWNERS));
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
