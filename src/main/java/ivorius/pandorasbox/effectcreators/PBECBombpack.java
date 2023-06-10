/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectEntitiesBombpack;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECBombpack implements PBEffectCreator
{
    public IValue time;
    public DValue range;

    public PBECBombpack(DValue range, IValue time)
    {
        this.range = range;
        this.time = time;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        int time = this.time.getValue(random);
        double range = this.range.getValue(random);

        return new PBEffectEntitiesBombpack(time, range);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.2f;
    }
}
