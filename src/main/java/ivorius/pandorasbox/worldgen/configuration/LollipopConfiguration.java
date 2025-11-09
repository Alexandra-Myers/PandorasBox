package ivorius.pandorasbox.worldgen.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.random.IValue;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public record LollipopConfiguration(IValue size, HolderSet<Block> blocks) implements FeatureConfiguration {
    public static final Codec<LollipopConfiguration> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(IValue.CODEC.fieldOf("size").forGetter(LollipopConfiguration::size),
                            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(LollipopConfiguration::blocks)
                    )
                    .apply(instance, LollipopConfiguration::new)
    );
}
