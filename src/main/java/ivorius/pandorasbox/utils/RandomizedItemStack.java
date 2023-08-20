package ivorius.pandorasbox.utils;

import ivorius.pandorasbox.weighted.WeightedSelector;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

/**
 * Created by lukas on 05.04.15.
 */
public class RandomizedItemStack implements WeightedSelector.Item
{
    public ItemStack itemStack;

    public int min;
    public int max;

    public double weight;

    public RandomizedItemStack(Item item, int min, int max, double weight)
    {
        this(new ItemStack(item, 1, new CompoundNBT()), min, max, weight);
    }

    public RandomizedItemStack(ItemStack itemStack, int min, int max, double weight)
    {
        this.itemStack = itemStack;
        this.min = min;
        this.max = max;
        this.weight = weight;
    }

    @Override
    public double weight()
    {
        return weight;
    }
}
