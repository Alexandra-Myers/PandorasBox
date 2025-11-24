package ivorius.pandorasbox.weighted;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.atlas.atlascore.util.Codecs;
import net.minecraft.resources.ResourceLocation;

public record WeightedResourceLocation(ResourceLocation resourceLocation, double weight) implements WeightedSelector.Item {
    public static final Codec<WeightedResourceLocation> CODEC = Codec.withAlternative(RecordCodecBuilder.create(instance ->
            instance.group(ResourceLocation.CODEC.fieldOf("location").forGetter(WeightedResourceLocation::resourceLocation),
                    Codecs.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").forGetter(WeightedResourceLocation::weight))
                    .apply(instance, WeightedResourceLocation::new)), ResourceLocation.CODEC.xmap(WeightedResourceLocation::new, WeightedResourceLocation::resourceLocation));
    public WeightedResourceLocation(ResourceLocation resourceLocation) {
        this(resourceLocation, 1.0);
    }
}