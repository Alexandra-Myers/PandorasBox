/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.weighted.WeightedSelector;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectSpawnItemStacks;
import ivorius.pandorasbox.random.*;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECSpawnItems implements PBEffectCreator {
    public IValue number;
    public IValue ticksPerItem;
    public List<RandomizedItemStack> items;
    public ValueThrow valueThrow;
    public ValueSpawn valueSpawn;

    public PBECSpawnItems(IValue number, IValue ticksPerItem, List<RandomizedItemStack> items, ValueThrow valueThrow, ValueSpawn valueSpawn) {
        this.number = number;
        this.ticksPerItem = ticksPerItem;
        this.items = items;
        this.valueThrow = valueThrow;
        this.valueSpawn = valueSpawn;
    }

    public PBECSpawnItems(IValue number, IValue ticksPerItem, List<RandomizedItemStack> items) {
        this(number, ticksPerItem, items, defaultThrow(), null);
    }

    public static ValueThrow defaultThrow() {
        return new ValueThrow(new DLinear(0.05, 0.2), new DLinear(0.2, 1.0));
    }

    public static ValueSpawn defaultShowerSpawn() {
        return new ValueSpawn(new DLinear(5.0, 30.0), new DConstant(150.0));
    }

    public static PBEffect constructEffect(RandomSource random, ItemStack[] stacks, int time, ValueThrow valueThrow, ValueSpawn valueSpawn) {
        boolean canSpawn = valueSpawn != null;
        boolean canThrow = valueThrow != null;

        if (canThrow && (!canSpawn || random.nextBoolean())) {
            PBEffectSpawnItemStacks effect = new PBEffectSpawnItemStacks(time, stacks);
            PBECSpawnEntities.setEffectThrow(effect, random, valueThrow);
            return effect;
        } else if (canSpawn) {
            PBEffectSpawnItemStacks effect = new PBEffectSpawnItemStacks(time, stacks);
            PBECSpawnEntities.setEffectSpawn(effect, random, valueSpawn);
            return effect;
        }

        throw new RuntimeException("Both spawnRange and throwStrength are null!");
    }

    public static ItemStack[] getItemStacks(RandomSource random, List<RandomizedItemStack> items, int number, boolean split, boolean mixUp, int enchantLevel, boolean giveNames) {
        ArrayList<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            RandomizedItemStack wrcc = mixUp ? WeightedSelector.selectItem(random, items) : items.get(i);
            ItemStack stack = wrcc.itemStack.copy();
            stack.setCount(wrcc.min + random.nextInt(wrcc.max - wrcc.min + 1));

            if (enchantLevel > 0) {
                List<EnchantmentInstance> enchantments = EnchantmentHelper.selectEnchantment(random, stack, enchantLevel, false);

                if (enchantments.isEmpty()) {
                    enchantments = EnchantmentHelper.selectEnchantment(random, new ItemStack(Items.IRON_AXE), enchantLevel, false);
                }

                if (!enchantments.isEmpty()) {
                    for (EnchantmentInstance enchantment : enchantments) {
                        if (stack.getItem() == Items.ENCHANTED_BOOK) {
                            EnchantedBookItem.addEnchantment(stack, enchantment);
                        } else {
                            stack.enchant(enchantment.enchantment, enchantment.level);
                        }
                    }
                }
            }

            if (giveNames) {
                stack.setHoverName(PandorasBoxItemNamer.getRandomName(random));
            }

            if (split) {
                for (int n = 0; n < stack.getCount(); n++) {
                    ItemStack splitStack = stack.split(1);
                    list.add(splitStack);
                }
            } else {
                list.add(stack);
            }
        }

        return list.toArray(new ItemStack[list.size()]);
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int number = this.number.getValue(random);
        int ticksPerItem = this.ticksPerItem.getValue(random);

        ItemStack[] stacks = getItemStacks(random, items, number, random.nextInt(3) != 0, true, 0, false);
        return constructEffect(random, stacks, number * ticksPerItem + 1, valueThrow, valueSpawn);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return 0.1f;
    }
}
