/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.weighted;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.world.item.ItemStack;

/**
 * Created by lukas on 31.03.14.
 */
public record WeightedSet(double weight, ItemStack[] set) implements WeightedSelector.Item {
    public static final Codec<WeightedSet> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.DOUBLE.fieldOf("weight").forGetter(WeightedSet::weight),
                            PBNBTHelper.arrayCodec(ItemStack.CODEC, () -> new ItemStack[0]).fieldOf("set").forGetter(WeightedSet::set))
                    .apply(instance, WeightedSet::new));
}
