/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectMulti;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECMulti implements PBEffectCreator
{
    public PBEffectCreator[] effects;
    public int[] delays;

    public PBECMulti(PBEffectCreator[] effects, int[] delays)
    {
        this.effects = effects;
        this.delays = delays;
    }

    public PBECMulti(Object... effectsAndDelays)
    {
        this.effects = new PBEffectCreator[effectsAndDelays.length / 2];
        this.delays = new int[this.effects.length];

        for (int i = 0; i < effects.length; i++)
        {
            effects[i] = (PBEffectCreator) effectsAndDelays[i * 2];
            delays[i] = (Integer) effectsAndDelays[i * 2 + 1];
        }
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        PBEffect[] createdEffects = new PBEffect[effects.length];

        for (int i = 0; i < effects.length; i++)
        {
            createdEffects[i] = effects[i].constructEffect(world, x, y, z, random);
        }

        return new PBEffectMulti(createdEffects, delays.clone());
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        float min = 999;
        for (PBEffectCreator effect : effects) {
            min = Math.min(effect.chanceForMoreEffects(world, x, y, z, random), min);
        }
        return min;
    }
}
