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
import net.minecraft.block.Block;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Random;

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
    public PBEffect constructEffect(World world, double x, double y, double z, Random random)
    {
        int rangeX = this.rangeX.getValue(random);
        int rangeY = this.rangeY.getValue(random);
        int rangeZ = this.rangeZ.getValue(random);
        int time = 6 * (rangeX * rangeY * rangeZ) + 50;

        Block platformBlock = PandorasBoxHelper.getRandomBlock(random, platformBlocks);

        return new PBEffectGenPool(time, rangeX, rangeZ, rangeY, rangeY, PandorasBoxHelper.getRandomUnifiedSeed(random), block, platformBlock);
    }

    @Override
    public float chanceForMoreEffects(World world, double x, double y, double z, Random random)
    {
        return 0.1f;
    }
}
