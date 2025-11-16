package ivorius.pandorasbox.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.weighted.WeightedSelector;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;
import java.util.function.Function;

/**
 * Created by lukas on 05.04.15.
 */
public record RandomizedItemStack(ItemStack itemStack, WeightedWithRandomCount count) implements WeightedSelector.Item {
    public static final Codec<ItemStack> SINGLE_ITEM_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            BuiltInRegistries.ITEM.byNameCodec().fieldOf("id").forGetter(ItemStack::getItem),
                            CompoundTag.CODEC.optionalFieldOf("tag").forGetter(itemStack -> Optional.ofNullable(itemStack.getTag()))
                    )
                    .apply(instance, (item, tag) -> {
                        ItemStack stack = new ItemStack(item);
                        tag.ifPresent(stack::setTag);
                        return stack;
                    })
    );
    public static final Codec<RandomizedItemStack> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(SINGLE_ITEM_CODEC.fieldOf("stack").forGetter(RandomizedItemStack::itemStack),
                            WeightedWithRandomCount.CODEC_FORCE.forGetter(RandomizedItemStack::count))
                    .apply(instance, RandomizedItemStack::new));
    public static final Codec<EitherArrayList<RandomizedItemStack, RandomizedItemTag>> LIST_CODEC = Codec.either(CODEC, RandomizedItemTag.TAG_MAP_CODEC).listOf().xmap(EitherArrayList::new, Function.identity());
    public RandomizedItemStack(ItemLike item, int min, int max, double weight) {
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
