/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenPool;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.weighted.WeightedBlock;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Collection;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECPool implements PBEffectCreator
{
    public DValue range;

    public Block block;
    public Collection<WeightedBlock> platformBlocks;

    public PBECPool(DValue range, Block block, Collection<WeightedBlock> platformBlocks)
    {
        this.range = range;
        this.block = block;
        this.platformBlocks = platformBlocks;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);

        Block platformBlock = platformBlocks.isEmpty() ? Blocks.AIR : PandorasBoxHelper.getRandomBlock(random, platformBlocks);

        return new PBEffectGenPool(time, range, PandorasBoxHelper.getRandomUnifiedSeed(random), block, platformBlock);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }
}
