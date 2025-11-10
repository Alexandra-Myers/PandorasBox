package ivorius.pandorasbox.effectcreators.generate.block_mappers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.generate.block_mappers.BlockMapper;
import ivorius.pandorasbox.effects.generate.block_mappers.RangeTaggedMapper;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public record RangeTaggedMapperCreator(TagKey<Block> tagKey, IValue complexity, DValue ringSize) implements BlockMapperCreator {
    public static final MapCodec<RangeTaggedMapperCreator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(RangeTaggedMapperCreator::tagKey),
                            IValue.CODEC.fieldOf("complexity").forGetter(RangeTaggedMapperCreator::complexity),
                            DValue.CODEC.fieldOf("ring_size").forGetter(RangeTaggedMapperCreator::ringSize))
                    .apply(instance, RangeTaggedMapperCreator::new));

    @Override
    public BlockMapper constructBlockMapper(Level world, double x, double y, double z, RandomSource random) {
        int complexity = this.complexity.getValue(random);
        double ringSize = this.ringSize.getValue(random);

        Integer[] metas = new Integer[complexity];
        for (int i = 0; i < metas.length; i++)
            metas[i] = random.nextInt(BuiltInRegistries.BLOCK.get(tagKey).map(HolderSet.ListBacked::size).orElse(16));
        return new RangeTaggedMapper(tagKey, metas, ringSize);
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapperCreator> codec() {
        return CODEC;
    }
}
