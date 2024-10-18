/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.weighted;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

/**
 * Created by lukas on 31.03.14.
 */
public class WeightedPotion implements WeightedSelector.Item
{
    public double weight;

    public Holder<MobEffect> potion;

    public int minStrength;
    public int maxStrength;

    public int minDuration;
    public int maxDuration;

    public WeightedPotion(double weight, Holder<MobEffect> potion, int minStrength, int maxStrength, int minDuration, int maxDuration)
    {
        this.weight = weight;
        this.potion = potion;
        this.minStrength = minStrength;
        this.maxStrength = maxStrength;
        this.minDuration = minDuration;
        this.maxDuration = maxDuration;
    }

    @Override
    public double weight()
    {
        return weight;
    }
}
