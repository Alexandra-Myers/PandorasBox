/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.random.*;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import ivorius.pandorasbox.utils.RandomizedItemTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECSpawnEnchantedItems(IValue number, IValue ticksPerItem, IValue enchantmentLevel, EitherArrayList<RandomizedItemStack, RandomizedItemTag> items, Optional<ValueThrow> valueThrow, Optional<ValueSpawn> valueSpawn, ZValue giveNames, ZValue spawnsFromEffectCenter) implements PBEffectCreator {
    public static final MapCodec<PBECSpawnEnchantedItems> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("number").forGetter(PBECSpawnEnchantedItems::number),
                            IValue.CODEC.fieldOf("ticks_per_item").forGetter(PBECSpawnEnchantedItems::ticksPerItem),
                            IValue.CODEC.fieldOf("enchantment_level").forGetter(PBECSpawnEnchantedItems::enchantmentLevel),
                            RandomizedItemStack.LIST_CODEC.fieldOf("items").forGetter(PBECSpawnEnchantedItems::items),
                            ValueThrow.CODEC.optionalFieldOf("value_throw").forGetter(PBECSpawnEnchantedItems::valueThrow),
                            ValueSpawn.CODEC.optionalFieldOf("value_spawn").forGetter(PBECSpawnEnchantedItems::valueSpawn),
                            ZValue.CODEC.optionalFieldOf("give_names", new ZChance(0.5)).forGetter(PBECSpawnEnchantedItems::giveNames),
                            ZValue.CODEC.fieldOf("spawns_from_effect_center").forGetter(PBECSpawnEnchantedItems::spawnsFromEffectCenter))
                    .apply(instance, PBECSpawnEnchantedItems::new));

    public PBECSpawnEnchantedItems(IValue number, IValue ticksPerItem, IValue enchantmentLevel, EitherArrayList<RandomizedItemStack, RandomizedItemTag> items, ZValue giveNames, ZValue spawnsFromEffectCenter) {
        this(number, ticksPerItem, enchantmentLevel, items, Optional.of(PBECSpawnItems.defaultThrow()), Optional.empty(), giveNames, spawnsFromEffectCenter);
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        int number = this.number.getValue(random);
        int enchantLevel = this.enchantmentLevel.getValue(random);
        int ticksPerItem = this.ticksPerItem.getValue(random);
        boolean giveNames = this.giveNames.getValue(random);

        ItemStack[] stacks = PBECSpawnItems.getItemStacks(random, world.registryAccess(), PandorasBoxHelper.assembleRandomisedStacks(BuiltInRegistries.ITEM, items), number, false, true, enchantLevel, giveNames, false);

        for (ItemStack stack : stacks)
            stack.setCount(1);

        return PBECSpawnItems.constructEffect(random, stacks, number * ticksPerItem + 1, valueThrow.orElse(null), valueSpawn.orElse(null), spawnsFromEffectCenter);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
