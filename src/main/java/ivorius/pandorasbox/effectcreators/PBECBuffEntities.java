/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectEntitiesBuff;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.weighted.WeightedSelector;
import ivorius.pandorasbox.weighted.WeightedPotion;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECBuffEntities implements PBEffectCreator
{
    public IValue time;
    public IValue number;
    public DValue range;

    public float chanceForMoreEffects;

    public Collection<WeightedPotion> applicablePotions;

    public PBECBuffEntities(IValue time, IValue number, DValue range, float chanceForMoreEffects, Collection<WeightedPotion> applicablePotions)
    {
        this.time = time;
        this.number = number;
        this.range = range;
        this.chanceForMoreEffects = chanceForMoreEffects;
        this.applicablePotions = applicablePotions;
    }

    @Override
    public PBEffect constructEffect(World world, double x, double y, double z, Random random)
    {
        int number = this.number.getValue(random);
        int time = this.time.getValue(random);
        double range = this.range.getValue(random);

        EffectInstance[] effects = new EffectInstance[number];
        for (int i = 0; i < effects.length; i++)
        {
            WeightedPotion weightedPotion = WeightedSelector.selectItem(random, applicablePotions);

            int duration = random.nextInt(weightedPotion.maxDuration - weightedPotion.minDuration + 1) + weightedPotion
                    .minDuration;
            int strength = random.nextInt(weightedPotion.maxStrength - weightedPotion.minStrength + 1) + weightedPotion
                    .minStrength;
            effects[i] = new EffectInstance(weightedPotion.potion, duration, strength, false, false);
        }

        return new PBEffectEntitiesBuff(time, range, effects);
    }

    @Override
    public float chanceForMoreEffects(World world, double x, double y, double z, Random random)
    {
        return chanceForMoreEffects;
    }
}
