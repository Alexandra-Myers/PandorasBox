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
public record PBECConvertToIce(DValue range) implements PBEffectCreator {
    public static final List<BlockMapper> ICE_MAPPERS;
    public static final List<EntitySpawner> ICE_SPAWNERS;
    static {
        ICE_MAPPERS = new ArrayList<>();
        ICE_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.WATER)}, Blocks.ICE));
        ICE_MAPPERS.add(new CoverMapper(Blocks.SNOW));
        ICE_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.right(BlockTags.FIRE)}, Blocks.AIR));
        ICE_MAPPERS.add(new LavaChillMapper(Blocks.LAVA, Blocks.ICE));
        ICE_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.MAGMA_BLOCK)}, Blocks.ICE));
        ICE_MAPPERS.add(new SimpleConvertMapper(new Either[] {Either.left(Blocks.LAVA)}, Blocks.PACKED_ICE));
        ICE_MAPPERS.add(new IceMapper());
        ICE_SPAWNERS = new ArrayList<>();
        ICE_SPAWNERS.add(new SpawnRandom("snow_golem", 1.0f / (20 * 20)));
    }
    public static final MapCodec<PBECConvertToIce> CODEC = DValue.CODEC.fieldOf("range").xmap(PBECConvertToIce::new, PBECConvertToIce::range);

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        return new PBEffectGenerate(time, range, 3, PandorasBoxHelper.getRandomUnifiedSeed(random), new SimpleConvertEffect(Optional.of(Biomes.FROZEN_PEAKS), ICE_MAPPERS, Collections.emptyList(), ICE_SPAWNERS));
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
