/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenConvertToCity;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.utils.StringConverter;
import ivorius.pandorasbox.weighted.WeightedEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECConvertToCity implements PBEffectCreator
{
    public Collection<WeightedEntity> entityIDs;
    public DValue range;

    public PBECConvertToCity(DValue range, Collection<WeightedEntity> entityIDs) {
        this.range = range;
        this.entityIDs = entityIDs;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        Collection<WeightedEntity> toChoose = new ArrayList<>();
        entityIDs.forEach(weightedEntity -> {
            if (!weightedEntity.entityID.startsWith("pbspecial"))
                toChoose.add(weightedEntity);
        });
        WeightedEntity[] entitySelection = PandorasBoxHelper.getRandomEntityList(random, toChoose);
        List<EntityType<?>> entities = new ArrayList<>();
        for (WeightedEntity entity : entitySelection) {
            String entityID = StringConverter.convertCamelCase(entity.entityID);
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(entityID));
            entities.add(type);
        }

        return new PBEffectGenConvertToCity(time, range, PandorasBoxHelper.getRandomUnifiedSeed(random), entities);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }
}
