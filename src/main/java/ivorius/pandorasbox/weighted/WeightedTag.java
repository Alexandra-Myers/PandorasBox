package ivorius.pandorasbox.weighted;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.atlas.atlascore.util.Codecs;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public record WeightedTag<T>(double weight, TagKey<T> tagKey) implements WeightedSelector.Item {
    public static <T> Codec<WeightedTag<T>> codec(ResourceKey<? extends Registry<T>> registryKey) {
        return RecordCodecBuilder.create(instance ->
                instance.group(Codecs.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").forGetter(WeightedTag::weight),
                                TagKey.hashedCodec(registryKey).fieldOf("tag").forGetter(WeightedTag::tagKey))
                        .apply(instance, WeightedTag::new));
    }
}
