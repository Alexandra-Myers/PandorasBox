package ivorius.pandorasbox.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.weighted.WeightedSelector;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/**
 * Created by lukas on 05.04.15.
 */
public record RandomizedItemTag(TagKey<Item> items, WeightedWithRandomCount count) implements WeightedSelector.Item {
    public static final Codec<RandomizedItemTag> TAG_MAP_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(TagKey.hashedCodec(Registries.ITEM).fieldOf("tag").forGetter(RandomizedItemTag::items),
                            WeightedWithRandomCount.CODEC.forGetter(RandomizedItemTag::count))
                    .apply(instance, RandomizedItemTag::new));

    public int min() {
        return count.min();
    }

    public int max() {
        return count.max().orElse(min());
    }

    @Override
    public double weight() {
        return count.weight();
    }
}
