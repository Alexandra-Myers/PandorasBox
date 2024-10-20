/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectSpawnBlocks;
import ivorius.pandorasbox.random.*;
import ivorius.pandorasbox.weighted.WeightedBlock;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECSpawnBlocks implements PBEffectCreator {
    public IValue number;
    public IValue ticksPerBlock;
    public ZValue spawnsFromEffectCenter;

    public Collection<WeightedBlock> blocks;
    public boolean shuffleBlocks = true;

    public ValueThrow valueThrow;
    public ValueSpawn valueSpawn;

    public PBECSpawnBlocks(IValue number, IValue ticksPerBlock, ZValue spawnsFromEffectCenter, Collection<WeightedBlock> blocks, ValueThrow valueThrow, ValueSpawn valueSpawn) {
        this.number = number;
        this.ticksPerBlock = ticksPerBlock;
        this.spawnsFromEffectCenter = spawnsFromEffectCenter;
        this.blocks = blocks;
        this.valueThrow = valueThrow;
        this.valueSpawn = valueSpawn;
    }

    public PBECSpawnBlocks(IValue number, IValue ticksPerBlock, ZValue spawnsFromEffectCenter, Collection<WeightedBlock> blocks) {
        this(number, ticksPerBlock, spawnsFromEffectCenter, blocks, defaultThrow(), null);
    }

    public static ValueThrow defaultThrow() {
        return new ValueThrow(new DLinear(0.2, 0.4), new DLinear(0.2, 1.0));
    }

    public static ValueSpawn defaultShowerSpawn() {
        return new ValueSpawn(new DLinear(5.0, 30.0), new DConstant(150.0));
    }

    public PBECSpawnBlocks setShuffleBlocks(boolean shuffle) {
        this.shuffleBlocks = shuffle;
        return this;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int number = this.number.getValue(random);
        int ticksPerBlock = this.ticksPerBlock.getValue(random);
        Block[] blocks;

        if (shuffleBlocks) {
            Block[] selection = PandorasBoxHelper.getRandomBlockList(random, this.blocks);
            blocks = constructBlocks(random, selection, number, true);
        } else {
            int max = 0;
            for (WeightedBlock weightedBlock : this.blocks)
                max += weightedBlock.weight;
            Block[] selection = new Block[max];
            max = 0;
            for (WeightedBlock weightedBlock : this.blocks)
            {
                for (int i = 0; i < weightedBlock.weight; i++)
                    selection[max + i] = weightedBlock.block;
                max += weightedBlock.weight;
            }
            blocks = constructBlocks(random, selection, number, true);
        }

        return constructEffect(random, blocks, number * ticksPerBlock + 1, valueThrow, valueSpawn, this.spawnsFromEffectCenter.getValue(random));
    }

    public static Block[] constructBlocks(RandomSource random, Block[] blocks, int number, boolean mixUp) {
        ArrayList<Block> list = new ArrayList<>();

        for (int i = 0; i < number; i++) {
            list.add(mixUp ? blocks[random.nextInt(blocks.length)] : blocks[i]);
        }

        return list.toArray(new Block[0]);
    }

    public static PBEffect constructEffect(RandomSource random, Block[] blocks, int time, ValueThrow valueThrow, ValueSpawn valueSpawn, boolean spawnsFromEffectCenter) {
        boolean canSpawn = valueSpawn != null;
        boolean canThrow = valueThrow != null;

        if (canThrow && (!canSpawn || random.nextBoolean())) {
            PBEffectSpawnBlocks effect = new PBEffectSpawnBlocks(time, blocks, !spawnsFromEffectCenter);
            PBECSpawnEntities.setEffectThrow(effect, random, valueThrow);
            return effect;
        } else if (canSpawn) {
            PBEffectSpawnBlocks effect = new PBEffectSpawnBlocks(time, blocks, !spawnsFromEffectCenter);
            PBECSpawnEntities.setEffectSpawn(effect, random, valueSpawn);
            return effect;
        }

        throw new RuntimeException("Both spawnRange and throwStrength are null!");
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return 0.1f;
    }
}
