/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectEntityBased;
import ivorius.pandorasbox.effects.entity.ThrowItemsEntityEffect;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import ivorius.pandorasbox.utils.RandomizedItemTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECThrowItems(IValue time, DValue range, DValue throwChancePerItem, DValue deletionChancePerThrow, IValue smuggledInItems, EitherArrayList<RandomizedItemStack, RandomizedItemTag> items) implements PBEffectCreator {
    public static final MapCodec<PBECThrowItems> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("time").forGetter(PBECThrowItems::time),
                            DValue.CODEC.fieldOf("range").forGetter(PBECThrowItems::range),
                            DValue.CODEC.fieldOf("throw_chance_per_item").forGetter(PBECThrowItems::throwChancePerItem),
                            DValue.CODEC.fieldOf("deletion_chance_per_throw").forGetter(PBECThrowItems::deletionChancePerThrow),
                            IValue.CODEC.fieldOf("smuggled_in_items").forGetter(PBECThrowItems::smuggledInItems),
                            RandomizedItemStack.LIST_CODEC.fieldOf("items").forGetter(PBECThrowItems::items))
                    .apply(instance, PBECThrowItems::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int time = this.time.getValue(random);
        double range = this.range.getValue(random);
        double chancePerItem = this.throwChancePerItem.getValue(random);
        double deletionChance = this.deletionChancePerThrow.getValue(random);
        int smuggledIn = this.smuggledInItems.getValue(random);

        ItemStack[] stacks = PBECSpawnItems.getItemStacks(random, world.registryAccess(), PandorasBoxHelper.assembleRandomisedStacks(BuiltInRegistries.ITEM, items), smuggledIn, random.nextInt(3) != 0, true, 0, false);

        return new PBEffectEntityBased(time, range, new ThrowItemsEntityEffect(chancePerItem, deletionChance, stacks));
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.15f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
