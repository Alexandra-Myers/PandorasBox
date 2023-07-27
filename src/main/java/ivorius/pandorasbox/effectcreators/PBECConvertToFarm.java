/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenConvertToFarm;
import ivorius.pandorasbox.random.DValue;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECConvertToFarm implements PBEffectCreator
{
    public DValue range;
    public DValue cropChance;

    public PBECConvertToFarm(DValue range, DValue cropChance)
    {
        this.range = range;
        this.cropChance = cropChance;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        double range = this.range.getValue(random);
        double cropChance = this.cropChance.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        return new PBEffectGenConvertToFarm(time, range, PandorasBoxHelper.getRandomUnifiedSeed(random), cropChance);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }
}
