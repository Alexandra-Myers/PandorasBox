/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenConvertToRainbowCloth;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECConvertToRainbowCloth implements PBEffectCreator
{
    public DValue range;
    public IValue rainbowComplexity;
    public DValue ringSize;

    public PBECConvertToRainbowCloth(DValue range, IValue rainbowComplexity, DValue ringSize)
    {
        this.range = range;
        this.rainbowComplexity = rainbowComplexity;
        this.ringSize = ringSize;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);
        int rainbowComplexity = this.rainbowComplexity.getValue(random);
        double ringSize = this.ringSize.getValue(random);

        int[] colors = new int[rainbowComplexity];
        for (int i = 0; i < colors.length; i++)
            colors[i] = random.nextInt(16);

        PBEffectGenConvertToRainbowCloth effect = new PBEffectGenConvertToRainbowCloth(time, range, PandorasBoxHelper.getRandomUnifiedSeed(random), colors, ringSize);
        return effect;
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }
}
