package ivorius.pandorasbox.effectcreators.generate.feature_generators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.generate.feature_generators.FeatureGenerator;
import ivorius.pandorasbox.effects.generate.feature_generators.GenerateHFT;
import ivorius.pandorasbox.random.IValue;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public record HFTGeneratorCreator(TagKey<Block> blocks, IValue variantCount) implements FeatureGeneratorCreator {
    public static final MapCodec<HFTGeneratorCreator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(TagKey.codec(Registries.BLOCK).fieldOf("blocks").forGetter(HFTGeneratorCreator::blocks),
                            IValue.CODEC.fieldOf("variant_count").forGetter(HFTGeneratorCreator::variantCount))
                    .apply(instance, HFTGeneratorCreator::new));

    @Override
    public FeatureGenerator constructFeatureGenerator(Level world, double x, double y, double z, RandomSource random) {
        Integer[] metaTypes = new Integer[variantCount.getValue(random)];
        for (int i = 0; i < metaTypes.length; i++) {
            metaTypes[i] = random.nextInt(BuiltInRegistries.BLOCK.get(blocks).map(HolderSet.ListBacked::size).orElse(32));
        }
        return new GenerateHFT(blocks, metaTypes);
    }

    @Override
    public @NotNull MapCodec<? extends FeatureGeneratorCreator> codec() {
        return CODEC;
    }
}
