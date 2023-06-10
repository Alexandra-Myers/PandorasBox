/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectRandomExplosions;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ValueHelper;
import ivorius.pandorasbox.random.ZValue;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECSpawnExplosions implements PBEffectCreator
{
    public IValue time;
    public IValue number;
    public DValue range;

    public DValue explosionStrength;

    public ZValue isFlaming;
    public ZValue isSmoking;

    public PBECSpawnExplosions(IValue time, IValue number, DValue range, DValue explosionStrength, ZValue isFlaming, ZValue isSmoking)
    {
        this.time = time;
        this.number = number;
        this.range = range;
        this.explosionStrength = explosionStrength;
        this.isFlaming = isFlaming;
        this.isSmoking = isSmoking;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        int time = this.time.getValue(random);
        int number = this.number.getValue(random);
        double range = this.range.getValue(random);
        double[] strength = ValueHelper.getValueRange(explosionStrength, random);
        boolean isFlaming = this.isFlaming.getValue(random);
        boolean isSmoking = this.isSmoking.getValue(random);

        PBEffectRandomExplosions effect = new PBEffectRandomExplosions(time, number, range, (float) strength[0], (float) strength[1], isFlaming, isSmoking);
        return effect;
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.7f;
    }
}
