/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenerate;
import ivorius.pandorasbox.effects.generate.SimpleConvertEffect;
import ivorius.pandorasbox.effects.generate.block_mappers.*;
import ivorius.pandorasbox.effects.generate.entity_spawners.EntitySpawner;
import ivorius.pandorasbox.effects.generate.entity_spawners.SpawnRandom;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.weighted.WeightedEntity;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECConvertToCity(DValue range, List<WeightedEntity> entityIDs) implements PBEffectCreator {
    public static final List<BlockMapper> CITY_MAPPERS;
    public static final List<EntitySpawner> CITY_SPAWNERS;
    public static final Either<Block, TagKey<Block>>[] CITY_TARGETS = new Either[] {Either.right(ConventionalBlockTags.STONES), Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(BlockTags.BASE_STONE_NETHER), Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(BlockTags.NYLIUM), Either.right(BlockTags.DIRT), Either.right(BlockTags.SAND), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE)};
    static {
        CITY_MAPPERS = new ArrayList<>();
        CITY_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.FLOWERS), Either.right(BlockTags.SNOW), Either.right(BlockTags.FIRE), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN), Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SEAGRASS), Either.left(Blocks.TALL_SEAGRASS)}, Blocks.AIR));
        CITY_MAPPERS.add(new CityMapper(CITY_TARGETS, Collections.emptyList()));
        CITY_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.right(PandorasBox.ALL_TERRACOTTA)}, Blocks.CYAN_TERRACOTTA));
        CITY_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.right(ConventionalBlockTags.OBSIDIANS), Either.left(Blocks.LAVA), Either.left(Blocks.ICE)}, Blocks.WATER));
        CITY_SPAWNERS = new ArrayList<>();
        CITY_SPAWNERS.add(new SpawnRandom("villager", 1.0f / (20 * 20)));
    }
    public static final MapCodec<PBECConvertToCity> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(DValue.CODEC.fieldOf("range").forGetter(PBECConvertToCity::range),
                            WeightedEntity.NO_SPECIAL_CODEC.listOf().fieldOf("entities").forGetter(PBECConvertToCity::entityIDs))
                    .apply(instance, PBECConvertToCity::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        WeightedEntity[] entitySelection = PandorasBoxHelper.getRandomEntityList(random, entityIDs);
        List<EntityType<?>> entities = new ArrayList<>();
        for (WeightedEntity entity : entitySelection) {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(ResourceLocation.tryParse(entity.entityID()));
            entities.add(type);
        }

        List<BlockMapper> adjMappers = new ArrayList<>(CITY_MAPPERS);
        adjMappers.set(1, new CityMapper(CITY_TARGETS, entities));

        return new PBEffectGenerate(time, range, 3, PandorasBoxHelper.getRandomUnifiedSeed(random), new SimpleConvertEffect(Optional.of(Biomes.PLAINS), adjMappers, Collections.emptyList(), CITY_SPAWNERS));
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
