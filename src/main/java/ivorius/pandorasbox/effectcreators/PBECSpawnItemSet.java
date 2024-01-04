/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.weighted.WeightedSelector;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ValueSpawn;
import ivorius.pandorasbox.random.ValueThrow;
import ivorius.pandorasbox.weighted.WeightedSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECSpawnItemSet implements PBEffectCreator {
    public IValue ticksPerItem;
    public List<WeightedSet> items;
    public ValueThrow valueThrow;
    public ValueSpawn valueSpawn;

    public PBECSpawnItemSet(IValue ticksPerItem, List<WeightedSet> items, ValueThrow valueThrow, ValueSpawn valueSpawn) {
        this.ticksPerItem = ticksPerItem;
        this.items = items;
        this.valueThrow = valueThrow;
        this.valueSpawn = valueSpawn;
    }

    public PBECSpawnItemSet(IValue ticksPerItem, List<WeightedSet> items) {
        this(ticksPerItem, items, PBECSpawnItems.defaultThrow(), null);
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int ticksPerItem = this.ticksPerItem.getValue(random);

        ItemStack[] itemSet = WeightedSelector.selectItem(random, items).set;
        ItemStack[] stacks = new ItemStack[itemSet.length];
        for (int i = 0; i < itemSet.length; i++) {
            stacks[i] = itemSet[i].copy();
        }

        return PBECSpawnItems.constructEffect(random, stacks, stacks.length * ticksPerItem + 1, valueThrow, valueSpawn);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }
}
