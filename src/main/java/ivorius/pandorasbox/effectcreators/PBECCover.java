/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenCover;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.ZValue;
import ivorius.pandorasbox.weighted.WeightedBlock;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Collection;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECCover implements PBEffectCreator
{
    public DValue range;
    public ZValue overSurface;

    public Collection<WeightedBlock> blocks;

    public PBECCover(DValue range, ZValue overSurface, Collection<WeightedBlock> blocks)
    {
        this.range = range;
        this.overSurface = overSurface;
        this.blocks = blocks;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        double range = this.range.getValue(random);
        boolean overSurface = this.overSurface.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        Block[] selection = PandorasBoxHelper.getRandomBlockList(random, blocks);

        PBEffectGenCover gen = new PBEffectGenCover(time, range, PandorasBoxHelper.getRandomUnifiedSeed(random), overSurface, selection);
        return gen;
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }
}
