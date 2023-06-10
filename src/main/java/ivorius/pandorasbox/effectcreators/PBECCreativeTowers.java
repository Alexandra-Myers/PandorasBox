/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenCreativeTowers;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.weighted.WeightedBlock;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Collection;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECCreativeTowers implements PBEffectCreator
{
    public DValue range;
    public IValue number;

    public Collection<WeightedBlock> blocks;

    public PBECCreativeTowers(DValue range, IValue number, Collection<WeightedBlock> blocks)
    {
        this.range = range;
        this.number = number;
        this.blocks = blocks;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        double range = this.range.getValue(random);
        int number = this.number.getValue(random);
        int time = Mth.floor((random.nextDouble() * 4.0 + 1.0) * number * 10.0);

        Block[] selection = PandorasBoxHelper.getRandomBlockList(random, blocks);

        PBEffectGenCreativeTowers genCreativeTowers = new PBEffectGenCreativeTowers(time);
        genCreativeTowers.createRandomStructures(random, number, range, blocks);
        return genCreativeTowers;
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.15f;
    }
}
