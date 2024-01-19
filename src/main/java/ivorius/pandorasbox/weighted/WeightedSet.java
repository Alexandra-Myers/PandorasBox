/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.weighted;

import net.minecraft.world.item.ItemStack;

/**
 * Created by lukas on 31.03.14.
 */
public class WeightedSet implements WeightedSelector.Item {
    public double weight;

    public ItemStack[] set;

    public WeightedSet(double weight, ItemStack[] set) {
        this.weight = weight;
        this.set = set;
    }

    @Override
    public double weight() {
        return weight;
    }
}
