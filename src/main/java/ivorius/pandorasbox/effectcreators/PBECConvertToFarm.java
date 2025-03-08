/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenerate;
import ivorius.pandorasbox.effects.generate.SimpleConvertEffect;
import ivorius.pandorasbox.effects.generate.block_mappers.CreateFarmMapper;
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
public record PBECConvertToFarm(DValue range, DValue cropChance) implements PBEffectCreator {
    public static final List<EntitySpawner> FARM_SPAWNERS;
    static {
        FARM_SPAWNERS = new ArrayList<>();
        FARM_SPAWNERS.add(new SpawnRandom("horse", 1.0f / (50 * 50)));
        FARM_SPAWNERS.add(new SpawnRandom("villager", 1.0f / (50 * 50)));
        FARM_SPAWNERS.add(new SpawnRandom("sheep", 1.0f / (50 * 50)));
        FARM_SPAWNERS.add(new SpawnRandom("pig", 1.0f / (50 * 50)));
        FARM_SPAWNERS.add(new SpawnRandom("cow", 1.0f / (50 * 50)));
        FARM_SPAWNERS.add(new SpawnRandom("chicken", 1.0f / (50 * 50)));
    }
    public static final MapCodec<PBECConvertToFarm> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(DValue.CODEC.fieldOf("range").forGetter(PBECConvertToFarm::range),
                            DValue.CODEC.fieldOf("crop_chance").forGetter(PBECConvertToFarm::cropChance))
                    .apply(instance, PBECConvertToFarm::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        double cropChance = this.cropChance.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        return new PBEffectGenerate(time, range, 3, PandorasBoxHelper.getRandomUnifiedSeed(random), new SimpleConvertEffect(Optional.of(Biomes.PLAINS), List.of(new CreateFarmMapper(cropChance)), Collections.emptyList(), FARM_SPAWNERS));
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
