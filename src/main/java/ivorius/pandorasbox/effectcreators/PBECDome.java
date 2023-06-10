/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenDome;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.weighted.WeightedBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Collection;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECDome implements PBEffectCreator
{
    public IValue time;
    public DValue range;

    public Collection<WeightedBlock> domeBlocks;
    public Block fillBlock;

    public PBECDome(IValue time, DValue range, Collection<WeightedBlock> domeBlocks, Block fillBlock)
    {
        this.time = time;
        this.range = range;
        this.domeBlocks = domeBlocks;
        this.fillBlock = fillBlock;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        double range = this.range.getValue(random);
        int time = this.time.getValue(random);

        Block domeBlock = PandorasBoxHelper.getRandomBlock(random, domeBlocks);

        PBEffectGenDome gen = new PBEffectGenDome(time, range, PandorasBoxHelper.getRandomUnifiedSeed(random), domeBlock, fillBlock);
        return gen;
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.15f;
    }
}
