/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenConvertToCity;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.weighted.WeightedEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECConvertToCity(DValue range, List<WeightedEntity> entityIDs) implements PBEffectCreator {
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

        return new PBEffectGenConvertToCity(time, range, PandorasBoxHelper.getRandomUnifiedSeed(random), entities);
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
