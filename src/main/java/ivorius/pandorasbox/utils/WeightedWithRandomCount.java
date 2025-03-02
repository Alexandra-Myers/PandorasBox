package ivorius.pandorasbox.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.atlas.atlascore.util.Codecs;

import java.util.Optional;

public record WeightedWithRandomCount(int min, Optional<Integer> max, double weight) {
    public static final MapCodec<WeightedWithRandomCount> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.optionalFieldOf("min_count", 1).forGetter(WeightedWithRandomCount::min),
                            Codec.INT.optionalFieldOf("max_count").forGetter(WeightedWithRandomCount::max),
                            Codecs.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").forGetter(WeightedWithRandomCount::weight))
                    .apply(instance, WeightedWithRandomCount::new));
    public static final MapCodec<WeightedWithRandomCount> CODEC_FORCE = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("min_count").forGetter(WeightedWithRandomCount::min),
                            Codec.INT.fieldOf("max_count").forGetter(weighted -> weighted.max.orElse(weighted.min)),
                            Codecs.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").forGetter(WeightedWithRandomCount::weight))
                    .apply(instance, WeightedWithRandomCount::new));
    public WeightedWithRandomCount(int min, int max, double weight) {
        this(min, Optional.of(max), weight);
    }
    public WeightedWithRandomCount copyWithMaxCountOverride(int newCount) {
        if (max.isPresent()) return this;
        return new WeightedWithRandomCount(min, Optional.of(newCount), weight);
    }
}
