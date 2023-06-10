/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenTargets;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.weighted.WeightedEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.Collection;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECTargets implements PBEffectCreator
{
    public IValue time;
    public DValue range;
    public DValue targetSize;
    public DValue entityDensity;

    public Collection<WeightedEntity> entities;

    public PBECTargets(IValue time, DValue range, DValue targetSize, DValue entityDensity, Collection<WeightedEntity> entities)
    {
        this.time = time;
        this.range = range;
        this.targetSize = targetSize;
        this.entityDensity = entityDensity;
        this.entities = entities;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        double range = this.range.getValue(random);
        double targetSize = this.targetSize.getValue(random);
        double entityDensity = this.entityDensity.getValue(random);
        int time = this.time.getValue(random);

        WeightedEntity entity = PandorasBoxHelper.getRandomEntityFromList(random, entities);

        PBEffectGenTargets gen = new PBEffectGenTargets(time, entity.entityID, range, targetSize, entityDensity);
        gen.createTargets(world, x, y, z, random);
        return gen;
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.15f;
    }
}
