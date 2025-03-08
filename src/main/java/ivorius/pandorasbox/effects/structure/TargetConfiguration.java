package ivorius.pandorasbox.effects.structure;

import com.mojang.serialization.Codec;
import ivorius.pandorasbox.utils.PBNBTHelper;
import org.jetbrains.annotations.NotNull;

public record TargetConfiguration(Integer[] colors) implements Structure.StructureConfiguration {
    public static final Codec<TargetConfiguration> CODEC = PBNBTHelper.arrayCodec(Codec.INT, () -> new Integer[0]).xmap(TargetConfiguration::new, TargetConfiguration::colors);
    @Override
    public @NotNull Codec<? extends Structure.StructureConfiguration> codec() {
        return CODEC;
    }
}
