/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenerate;
import ivorius.pandorasbox.effects.generate.SimpleConvertEffect;
import ivorius.pandorasbox.effects.generate.block_mappers.BlockMapper;
import ivorius.pandorasbox.effects.generate.block_mappers.HalloweenMapper;
import ivorius.pandorasbox.effects.generate.entity_spawners.EntitySpawner;
import ivorius.pandorasbox.effects.generate.entity_spawners.SpawnRandom;
import ivorius.pandorasbox.random.DValue;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECConvertToHalloween(DValue range) implements PBEffectCreator {
    public static final List<BlockMapper> HALLOWEEN_MAPPERS;
    public static final List<EntitySpawner> HALLOWEEN_SPAWNERS;
    static {
        HALLOWEEN_MAPPERS = new ArrayList<>();
        HALLOWEEN_MAPPERS.add(new HalloweenMapper());
        HALLOWEEN_SPAWNERS = new ArrayList<>();
        HALLOWEEN_SPAWNERS.add(new SpawnRandom("zombified_piglin", 1.0f / (20 * 20)));
        HALLOWEEN_SPAWNERS.add(new SpawnRandom("enderman", 1.0f / (20 * 20)));
        HALLOWEEN_SPAWNERS.add(new SpawnRandom("phantom", 1.0f / (20 * 20)));
    }
    public static final MapCodec<PBECConvertToHalloween> CODEC = DValue.CODEC.fieldOf("range").xmap(PBECConvertToHalloween::new, PBECConvertToHalloween::range);

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        return new PBEffectGenerate(time, range, 3, PandorasBoxHelper.getRandomUnifiedSeed(random), new SimpleConvertEffect(Optional.of(Biomes.SOUL_SAND_VALLEY), HALLOWEEN_MAPPERS, Collections.emptyList(), HALLOWEEN_SPAWNERS));
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
