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
import ivorius.pandorasbox.effects.generate.block_mappers.*;
import ivorius.pandorasbox.effects.generate.entity_spawners.EntitySpawner;
import ivorius.pandorasbox.effects.generate.entity_spawners.SpawnRandom;
import ivorius.pandorasbox.random.DValue;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECConvertToChristmas(DValue range) implements PBEffectCreator {
    public static final List<BlockMapper> CHRISTMAS_MAPPERS;
    public static final List<EntitySpawner> CHRISTMAS_SPAWNERS;
    static {
        CHRISTMAS_MAPPERS = new ArrayList<>();
        CHRISTMAS_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.WATER)}, Blocks.ICE));
        CHRISTMAS_MAPPERS.add(new ChrismasGiftsMapper());
        CHRISTMAS_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.FIRE)}, Blocks.AIR));
        CHRISTMAS_MAPPERS.add(new LavaChillMapper(Blocks.LAVA, Blocks.COBBLESTONE));
        CHRISTMAS_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.MAGMA_BLOCK)}, Blocks.COBBLESTONE));
        CHRISTMAS_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.LAVA)}, Blocks.OBSIDIAN));
        CHRISTMAS_SPAWNERS = new ArrayList<>();
        CHRISTMAS_SPAWNERS.add(new SpawnRandom("pbspecial_hogfather", 1.0f / (150 * 150)));
        CHRISTMAS_SPAWNERS.add(new SpawnRandom("snow_golem", 1.0f / (20 * 20)));
    }
    public static final MapCodec<PBECConvertToChristmas> CODEC = DValue.CODEC.fieldOf("range").xmap(PBECConvertToChristmas::new, PBECConvertToChristmas::range);

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        return new PBEffectGenerate(time, range, 3, PandorasBoxHelper.getRandomUnifiedSeed(random), new SimpleConvertEffect(Optional.of(Biomes.SNOWY_TAIGA), CHRISTMAS_MAPPERS, Collections.emptyList(), CHRISTMAS_SPAWNERS));
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
