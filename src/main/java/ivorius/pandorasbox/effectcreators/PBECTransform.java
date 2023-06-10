/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenTransform;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.weighted.WeightedBlock;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Collection;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECTransform implements PBEffectCreator
{
    public DValue range;

    public Collection<WeightedBlock> blocks;

    public PBECTransform(DValue range, Collection<WeightedBlock> blocks)
    {
        this.range = range;
        this.blocks = blocks;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        Block[] selection = PandorasBoxHelper.getRandomBlockList(random, blocks);

        PBEffectGenTransform genTransform = new PBEffectGenTransform(time, range, PandorasBoxHelper.getRandomUnifiedSeed(random), selection);
        return genTransform;
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }
}
