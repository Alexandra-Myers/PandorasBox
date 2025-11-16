package ivorius.pandorasbox.weighted;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.atlas.atlascore.util.Codecs;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.Arrays;
import java.util.List;

public record WeightedResourceKey<T>(double weight, ResourceKey<T> key) implements WeightedSelector.Item {
    public static <T> Codec<WeightedResourceKey<T>> codec(ResourceKey<? extends Registry<T>> registryKey) {
        return PBNBTHelper.withAlternative(RecordCodecBuilder.create(instance ->
                instance.group(Codecs.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").forGetter(WeightedResourceKey::weight),
                                ResourceKey.codec(registryKey).fieldOf("key").forGetter(WeightedResourceKey::key))
                        .apply(instance, WeightedResourceKey::new)), ResourceKey.codec(registryKey), resourceKey -> new WeightedResourceKey<>(1.0, resourceKey));
    }
    public WeightedResourceKey(ResourceKey<T> key) {
        this(1.0, key);
    }
    @SafeVarargs
    public static <T> List<WeightedResourceKey<T>> ofKeysEqualWeight(ResourceKey<T>... keys) {
        return Arrays.stream(keys).map(WeightedResourceKey::new).toList();
    }
}
