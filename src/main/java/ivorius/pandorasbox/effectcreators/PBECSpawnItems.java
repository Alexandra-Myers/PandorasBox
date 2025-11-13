/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectSpawnEntities;
import ivorius.pandorasbox.effects.spawn_entities.EntitySpawnConfiguration;
import ivorius.pandorasbox.effects.spawn_entities.SpawnItemStacksEffect;
import ivorius.pandorasbox.random.*;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import ivorius.pandorasbox.utils.RandomizedItemTag;
import ivorius.pandorasbox.weighted.WeightedSelector;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECSpawnItems(IValue number, IValue ticksPerItem, EitherArrayList<RandomizedItemStack, RandomizedItemTag> items, ZValue canBeFood, ZValue spawnsFromEffectCenter, Optional<ValueThrow> valueThrow, Optional<ValueSpawn> valueSpawn) implements PBEffectCreator {
    public static final MapCodec<PBECSpawnItems> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("number").forGetter(PBECSpawnItems::number),
                            IValue.CODEC.fieldOf("ticks_per_item").forGetter(PBECSpawnItems::ticksPerItem),
                            RandomizedItemStack.LIST_CODEC.fieldOf("items").forGetter(PBECSpawnItems::items),
                            ZValue.CODEC.optionalFieldOf("can_be_food", new ZConstant(false)).forGetter(PBECSpawnItems::canBeFood),
                            ZValue.CODEC.fieldOf("spawns_from_effect_center").forGetter(PBECSpawnItems::spawnsFromEffectCenter),
                            ValueThrow.CODEC.optionalFieldOf("value_throw").forGetter(PBECSpawnItems::valueThrow),
                            ValueSpawn.CODEC.optionalFieldOf("value_spawn").forGetter(PBECSpawnItems::valueSpawn))
                    .apply(instance, PBECSpawnItems::new));

    public static ValueThrow defaultThrow() {
        return new ValueThrow(new DLinear(0.05, 0.2), new DLinear(0.2, 1.0));
    }


    public static PBEffect constructEffect(RandomSource random, ItemStack[] stacks, int time, ValueThrow valueThrow, ValueSpawn valueSpawn, ZValue spawnsFromEffectCenter) {
        boolean canSpawn = valueSpawn != null;
        boolean canThrow = valueThrow != null;
        EntitySpawnConfiguration.Builder builder = EntitySpawnConfiguration.builder(!spawnsFromEffectCenter.getValue(random));

        if (canThrow && (!canSpawn || random.nextBoolean())) return new PBEffectSpawnEntities(time, stacks.length, new SpawnItemStacksEffect(stacks, PBECSpawnEntities.setEffectThrow(builder, random, valueThrow).build()));
        else if (canSpawn) return new PBEffectSpawnEntities(time, stacks.length, new SpawnItemStacksEffect(stacks, PBECSpawnEntities.setEffectSpawn(builder, random, valueSpawn).build()));

        throw new RuntimeException("Both spawnRange and throwStrength are null!");
    }

    public static ItemStack[] getItemStacks(RandomSource random, RegistryAccess registryAccess, List<RandomizedItemStack> items, int number, boolean split, boolean mixUp, int enchantLevel, boolean giveNames, boolean isFood) {
        ItemStack[] stacks = new ItemStack[number];
        for (int i = 0; i < number; i++) {
            RandomizedItemStack wrcc = mixUp ? WeightedSelector.selectItem(random, items) : items.get(i);
            ItemStack stack = wrcc.itemStack().copy();
            if (wrcc.max() > stack.getMaxStackSize()) stack.set(DataComponents.MAX_STACK_SIZE, wrcc.max());
            if (isFood) PandorasBoxHelper.createRandomFoodProperties(stack, random);
            stack.setCount(wrcc.min() + random.nextInt(wrcc.max() - wrcc.min() + 1));
            enchantItemStack(registryAccess, enchantLevel, random, stack);

            if (giveNames) {
                stack.set(DataComponents.ITEM_NAME, PandorasBoxItemNamer.getRandomName(random));
            }

            if (split) {
                stacks[i] = stack.split(1);
            } else {
                stacks[i] = stack;
            }
        }

        return stacks;
    }

    public static void enchantItemStack(RegistryAccess registryAccess, int enchantLevel, RandomSource random, ItemStack stack) {
        Registry<Enchantment> enchantmentRegistry = registryAccess.registryOrThrow(Registries.ENCHANTMENT);

        if (enchantLevel > 0) {
            List<EnchantmentInstance> enchantments = EnchantmentHelper.selectEnchantment(random, stack, enchantLevel, enchantmentRegistry.holders().map(enchantmentReference -> enchantmentReference));

            if (enchantments.isEmpty()) {
                enchantments = EnchantmentHelper.selectEnchantment(random, new ItemStack(Items.BOOK), enchantLevel, enchantmentRegistry.holders().map(enchantmentReference -> enchantmentReference));
            }

            if (!enchantments.isEmpty()) {
                for (EnchantmentInstance enchantment : enchantments) {
                    stack.enchant(enchantment.enchantment, enchantment.level);
                }
            }
        }
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int number = this.number.getValue(random);
        int ticksPerItem = this.ticksPerItem.getValue(random);
        boolean isFood = this.canBeFood.getValue(random);

        ItemStack[] stacks = getItemStacks(random, world.registryAccess(), PandorasBoxHelper.assembleRandomisedStacks(BuiltInRegistries.ITEM, BuiltInRegistries.BLOCK, items), number, random.nextInt(3) != 0, true, 0, false, isFood);
        return constructEffect(random, stacks, number * ticksPerItem + 1, valueThrow.orElse(null), valueSpawn.orElse(null), spawnsFromEffectCenter);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return 0.1f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
