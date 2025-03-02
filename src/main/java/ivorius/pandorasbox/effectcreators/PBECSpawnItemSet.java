/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.random.ZValue;
import ivorius.pandorasbox.weighted.WeightedSelector;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ValueSpawn;
import ivorius.pandorasbox.random.ValueThrow;
import ivorius.pandorasbox.weighted.WeightedSet;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECSpawnItemSet(IValue ticksPerItem, ZValue spawnsFromEffectCenter, List<WeightedSet> items, Optional<ValueThrow> valueThrow, Optional<ValueSpawn> valueSpawn) implements PBEffectCreator {
    public static final MapCodec<PBECSpawnItemSet> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("ticks_per_item").forGetter(PBECSpawnItemSet::ticksPerItem),
                            ZValue.CODEC.fieldOf("spawn_from_effect_center").forGetter(PBECSpawnItemSet::spawnsFromEffectCenter),
                            WeightedSet.CODEC.listOf().fieldOf("sets").forGetter(PBECSpawnItemSet::items),
                            ValueThrow.CODEC.optionalFieldOf("value_throw").forGetter(PBECSpawnItemSet::valueThrow),
                            ValueSpawn.CODEC.optionalFieldOf("value_spawn").forGetter(PBECSpawnItemSet::valueSpawn))
                    .apply(instance, PBECSpawnItemSet::new));
    public PBECSpawnItemSet(IValue ticksPerItem, ZValue spawnsFromEffectCenter, List<WeightedSet> items) {
        this(ticksPerItem, spawnsFromEffectCenter, items, Optional.of(PBECSpawnItems.defaultThrow()), Optional.empty());
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int ticksPerItem = this.ticksPerItem.getValue(random);

        ItemStack[] itemSet = WeightedSelector.selectItem(random, items).set();
        ItemStack[] stacks = new ItemStack[itemSet.length];
        for (int i = 0; i < itemSet.length; i++) {
            stacks[i] = itemSet[i].copy();
        }

        return PBECSpawnItems.constructEffect(random, stacks, stacks.length * ticksPerItem + 1, valueThrow.orElse(null), valueSpawn.orElse(null), spawnsFromEffectCenter);
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
