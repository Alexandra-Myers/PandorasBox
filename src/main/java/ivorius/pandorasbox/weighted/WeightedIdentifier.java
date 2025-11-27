package ivorius.pandorasbox.weighted;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.atlas.atlascore.util.Codecs;
import net.minecraft.resources.Identifier;

public record WeightedIdentifier(Identifier identifier, double weight) implements WeightedSelector.Item {
    public static final Codec<WeightedIdentifier> CODEC = Codec.withAlternative(RecordCodecBuilder.create(instance ->
            instance.group(Identifier.CODEC.fieldOf("location").forGetter(WeightedIdentifier::identifier),
                    Codecs.doubleRange(0, Double.MAX_VALUE).fieldOf("weight").forGetter(WeightedIdentifier::weight))
                    .apply(instance, WeightedIdentifier::new)), Identifier.CODEC.xmap(WeightedIdentifier::new, WeightedIdentifier::identifier));
    public WeightedIdentifier(Identifier identifier) {
        this(identifier, 1.0);
    }
}