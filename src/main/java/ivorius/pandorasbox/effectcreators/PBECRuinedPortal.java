/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenRuinedPortal;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedSelector;
import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECRuinedPortal implements PBEffectCreator {
    public IValue rangeH;
    public IValue rangeY;
    public IValue rangeStartY;

    public Block block;
    public final WeightedBlock[][] bricks;
    public final ArrayListExtensions<RandomizedItemStack> loot;

    public PBECRuinedPortal(IValue rangeH, IValue rangeY, IValue rangeStartY, WeightedBlock[][] brickSet, ArrayListExtensions<RandomizedItemStack> loot) {
        this.rangeH = rangeH;
        this.rangeY = rangeY;
        this.rangeStartY = rangeStartY;
        this.bricks = brickSet;
        this.loot = loot;
    }

    @Override
    public PBEffect constructEffect(World world, double x, double y, double z, Random random) {
        int rangeH = this.rangeH.getValue(random);
        int rangeY = this.rangeY.getValue(random);
        int rangeStartY = this.rangeStartY.getValue(random);
        rangeY += rangeStartY;
        int time = rangeH * rangeH * rangeY;

        WeightedBlock[] bricks = WeightedSelector.selectWeightless(random, Arrays.asList(this.bricks), this.bricks.length);
        Direction.Axis axis = random.nextBoolean() ? Direction.Axis.X : Direction.Axis.Z;

        return new PBEffectGenRuinedPortal(time, rangeH, rangeY, rangeStartY, PandorasBoxHelper.getRandomUnifiedSeed(random), bricks, loot, axis);
    }

    @Override
    public float chanceForMoreEffects(World world, double x, double y, double z, Random random)
    {
        return 0.1f;
    }
}
