/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ValueSpawn;
import ivorius.pandorasbox.random.ValueThrow;
import ivorius.pandorasbox.random.ZValue;
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
public record PBECSpawnManySameItems(IValue ticksPerStack, ZValue spawnsFromEffectCenter, EitherArrayList<RandomizedItemStack, RandomizedItemTag> items, Optional<ValueThrow> valueThrow, Optional<ValueSpawn> valueSpawn) implements PBEffectCreator {
    public static final MapCodec<PBECSpawnManySameItems> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("ticks_per_stack").forGetter(PBECSpawnManySameItems::ticksPerStack),
                            ZValue.CODEC.fieldOf("spawns_from_effect_center").forGetter(PBECSpawnManySameItems::spawnsFromEffectCenter),
                            RandomizedItemStack.LIST_CODEC.fieldOf("items").forGetter(PBECSpawnManySameItems::items),
                            ValueThrow.CODEC.optionalFieldOf("value_throw").forGetter(PBECSpawnManySameItems::valueThrow),
                            ValueSpawn.CODEC.optionalFieldOf("value_spawn").forGetter(PBECSpawnManySameItems::valueSpawn))
                    .apply(instance, PBECSpawnManySameItems::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int ticksPerStack = this.ticksPerStack.getValue(random);
        int number = random.nextInt(5) + 5;

        ItemStack[] stacks = PBECSpawnItems.getItemStacks(random, world.registryAccess(), PandorasBoxHelper.assembleRandomisedStacks(BuiltInRegistries.ITEM, items), number, true, true, 0, false);
        return PBECSpawnItems.constructEffect(random, stacks, number * ticksPerStack + 1, valueThrow.orElse(null), valueSpawn.orElse(null), spawnsFromEffectCenter);
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
