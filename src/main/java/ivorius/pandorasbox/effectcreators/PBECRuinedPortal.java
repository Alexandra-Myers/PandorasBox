/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenPool;
import ivorius.pandorasbox.effects.PBEffectGenRuinedPortal;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedSelector;
import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECRuinedPortal implements PBEffectCreator
{
    public IValue rangeX;
    public IValue rangeY;
    public IValue rangeZ;
    public IValue rangeStartY;

    public Block block;
    public final Collection<ArrayListExtensions<WeightedBlock>> bricks;
    public final ArrayListExtensions<RandomizedItemStack> loot;
    public final Direction.Axis axis;

    public PBECRuinedPortal(IValue rangeX, IValue rangeY, IValue rangeZ, IValue rangeStartY, Collection<ArrayListExtensions<WeightedBlock>> brickSet, ArrayListExtensions<RandomizedItemStack> loot, Direction.Axis axis)
    {
        this.rangeX = rangeX;
        this.rangeY = rangeY;
        this.rangeZ = rangeZ;
        this.rangeStartY = rangeStartY;
        this.bricks = brickSet;
        this.loot = loot;
        this.axis = axis;
    }

    @Override
    public PBEffect constructEffect(World world, double x, double y, double z, Random random)
    {
        int rangeX = this.rangeX.getValue(random);
        int rangeY = this.rangeY.getValue(random);
        int rangeStartY = this.rangeStartY.getValue(random);
        int rangeZ = this.rangeZ.getValue(random);
        rangeY += rangeStartY;
        int time = 3 * (rangeX * rangeY * rangeZ) + 10;

        ArrayListExtensions<WeightedBlock> bricks = WeightedSelector.selectWeightless(random, this.bricks, this.bricks.size());

        return new PBEffectGenRuinedPortal(time, rangeX, rangeZ, rangeY, rangeStartY, PandorasBoxHelper.getRandomUnifiedSeed(random), bricks, loot, axis);
    }

    @Override
    public float chanceForMoreEffects(World world, double x, double y, double z, Random random)
    {
        return 0.1f;
    }
}
