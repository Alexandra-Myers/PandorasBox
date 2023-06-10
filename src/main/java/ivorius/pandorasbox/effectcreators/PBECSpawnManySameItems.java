/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ValueSpawn;
import ivorius.pandorasbox.random.ValueThrow;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECSpawnManySameItems implements PBEffectCreator
{
    public IValue ticksPerStack;
    public List<RandomizedItemStack> items;
    public ValueThrow valueThrow;
    public ValueSpawn valueSpawn;

    public PBECSpawnManySameItems(IValue ticksPerStack, List<RandomizedItemStack> items, ValueThrow valueThrow, ValueSpawn valueSpawn)
    {
        this.ticksPerStack = ticksPerStack;
        this.items = items;
        this.valueThrow = valueThrow;
        this.valueSpawn = valueSpawn;
    }

    public PBECSpawnManySameItems(IValue ticksPerStack, List<RandomizedItemStack> items)
    {
        this(ticksPerStack, items, PBECSpawnItems.defaultThrow(), null);
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        int ticksPerStack = this.ticksPerStack.getValue(random);
        int number = random.nextInt(5) + 5;

        ItemStack[] stacks = PBECSpawnItems.getItemStacks(random, items, number, true, true, 0, false);
        return PBECSpawnItems.constructEffect(random, stacks, number * ticksPerStack + 1, valueThrow, valueSpawn);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }
}
