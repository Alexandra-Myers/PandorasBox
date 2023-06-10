/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenPool;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.weighted.WeightedBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Collection;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECPool implements PBEffectCreator
{
    public IValue rangeX;
    public IValue rangeY;
    public IValue rangeZ;

    public Block block;
    public Collection<WeightedBlock> platformBlocks;

    public PBECPool(IValue rangeX, IValue rangeY, IValue rangeZ, Block block, Collection<WeightedBlock> platformBlocks)
    {
        this.rangeX = rangeX;
        this.rangeY = rangeY;
        this.rangeZ = rangeZ;
        this.block = block;
        this.platformBlocks = platformBlocks;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        int rangeX = this.rangeX.getValue(random);
        int rangeY = this.rangeY.getValue(random);
        int rangeZ = this.rangeZ.getValue(random);
        int time = 6 * (rangeX * rangeY * rangeZ) + 50;

        Block platformBlock = PandorasBoxHelper.getRandomBlock(random, platformBlocks);

        return new PBEffectGenPool(time, rangeX, rangeZ, rangeY, rangeY, PandorasBoxHelper.getRandomUnifiedSeed(random), block, platformBlock);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }
}
