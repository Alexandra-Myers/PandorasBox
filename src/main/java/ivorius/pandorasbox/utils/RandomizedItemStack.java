package ivorius.pandorasbox.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.weighted.WeightedSelector;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

/**
 * Created by lukas on 05.04.15.
 */
public record RandomizedItemStack(ItemStack itemStack, WeightedWithRandomCount count) implements WeightedSelector.Item {
    public static final Codec<RandomizedItemStack> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(ItemStack.SINGLE_ITEM_CODEC.fieldOf("stack").forGetter(RandomizedItemStack::itemStack),
                            WeightedWithRandomCount.CODEC_FORCE.forGetter(RandomizedItemStack::count))
                    .apply(instance, RandomizedItemStack::new));
    public static final Codec<EitherArrayList<RandomizedItemStack, RandomizedItemTag>> LIST_CODEC = Codec.either(CODEC, RandomizedItemTag.TAG_MAP_CODEC).listOf().xmap(EitherArrayList::new, Function.identity());
    public RandomizedItemStack(Item item, int min, int max, double weight) {
        this(new ItemStack(item, 1), new WeightedWithRandomCount(min, max, weight));
    }

    public int min() {
        return count.min();
    }

    public int max() {
        return count.max().orElse(-1); // Impossible
    }

    @Override
    public double weight() {
        return count.weight();
    }
}
