/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectDuplicateBox;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECDuplicateBox implements PBEffectCreator
{
    public IValue spawnMode;
    public DValue moreEffectChance;

    public PBECDuplicateBox(IValue spawnMode, DValue moreEffectChance)
    {
        this.spawnMode = spawnMode;
        this.moreEffectChance = moreEffectChance;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        int spawnMode = this.spawnMode.getValue(random);
        return new PBEffectDuplicateBox(spawnMode);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return (float) moreEffectChance.getValue(random);
    }
}
