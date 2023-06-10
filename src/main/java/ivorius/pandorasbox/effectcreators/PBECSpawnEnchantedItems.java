/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ValueSpawn;
import ivorius.pandorasbox.random.ValueThrow;
import ivorius.pandorasbox.random.ZValue;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECSpawnEnchantedItems implements PBEffectCreator
{
    public IValue number;
    public IValue ticksPerItem;
    public IValue enchantmentLevel;
    public List<RandomizedItemStack> items;
    public ValueThrow valueThrow;
    public ValueSpawn valueSpawn;

    public ZValue giveNames;

    public PBECSpawnEnchantedItems(IValue number, IValue ticksPerItem, IValue enchantmentLevel, List<RandomizedItemStack> items, ValueThrow valueThrow, ValueSpawn valueSpawn, ZValue giveNames)
    {
        this.number = number;
        this.ticksPerItem = ticksPerItem;
        this.enchantmentLevel = enchantmentLevel;
        this.items = items;
        this.valueThrow = valueThrow;
        this.valueSpawn = valueSpawn;
        this.giveNames = giveNames;
    }

    public PBECSpawnEnchantedItems(IValue number, IValue ticksPerItem, IValue enchantmentLevel, List<RandomizedItemStack> items, ZValue giveNames)
    {
        this(number, ticksPerItem, enchantmentLevel, items, PBECSpawnItems.defaultThrow(), null, giveNames);
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        int number = this.number.getValue(random);
        int enchantLevel = this.enchantmentLevel.getValue(random);
        int ticksPerItem = this.ticksPerItem.getValue(random);
        boolean giveNames = this.giveNames.getValue(random);

        ItemStack[] stacks = PBECSpawnItems.getItemStacks(random, items, number, false, true, enchantLevel, giveNames);

        for (ItemStack stack : stacks)
            stack.setCount(1);

        return PBECSpawnItems.constructEffect(random, stacks, number * ticksPerItem + 1, valueThrow, valueSpawn);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }
}
