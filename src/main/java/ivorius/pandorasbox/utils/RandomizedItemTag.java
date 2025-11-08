package ivorius.pandorasbox.utils;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.weighted.WeightedSelector;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Created by lukas on 05.04.15.
 */
public record RandomizedItemTag(Either<TagKey<Item>, TagKey<Block>> items, WeightedWithRandomCount count) implements WeightedSelector.Item {
    public static final Codec<RandomizedItemTag> TAG_MAP_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.either(TagKey.hashedCodec(Registries.ITEM), TagKey.hashedCodec(Registries.BLOCK)).fieldOf("tag").forGetter(RandomizedItemTag::items),
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
