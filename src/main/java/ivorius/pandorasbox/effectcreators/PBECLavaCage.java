/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenLavaCages;
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
public class PBECLavaCage implements PBEffectCreator {
    public DValue range;

    public Block lavaBlock;
    public Block fillBlock;
    public Collection<WeightedBlock> cageBlocks;
    public Collection<WeightedBlock> floorBlocks;

    public PBECLavaCage(DValue range, Block lavaBlock, Block fillBlock, Collection<WeightedBlock> cageBlocks, Collection<WeightedBlock> floorBlocks) {
        this.range = range;
        this.lavaBlock = lavaBlock;
        this.fillBlock = fillBlock;
        this.cageBlocks = cageBlocks;
        this.floorBlocks = floorBlocks;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        Block cageBlock = PandorasBoxHelper.getRandomBlock(random, cageBlocks);
        Block floorBlock = PandorasBoxHelper.getRandomBlock(random, floorBlocks);

        return new PBEffectGenLavaCages(time, range, PandorasBoxHelper.getRandomUnifiedSeed(random), lavaBlock, cageBlock, fillBlock, floorBlock);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }
}
