package ivorius.pandorasbox.effectcreators.generate.block_mappers;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.generate.block_mappers.BlockMapper;
import ivorius.pandorasbox.effects.generate.block_mappers.RandomTaggedMapper;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record RandomTaggedMapperCreator(Optional<Either<Block, TagKey<Block>>[]> targets, TagKey<Block> toReplace, IValue variantCount) implements BlockMapperCreator {
    @SuppressWarnings({"unchecked", "RedundantCast"})
    public static final MapCodec<RandomTaggedMapperCreator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(Codec.either(BuiltInRegistries.BLOCK.byNameCodec(), TagKey.hashedCodec(Registries.BLOCK)), () -> (Either<Block, TagKey<Block>>[]) new Either[0]).optionalFieldOf("targets").forGetter(RandomTaggedMapperCreator::targets),
                            TagKey.codec(Registries.BLOCK).fieldOf("to_replace").forGetter(RandomTaggedMapperCreator::toReplace),
                            IValue.CODEC.fieldOf("variant_count").forGetter(RandomTaggedMapperCreator::variantCount))
                    .apply(instance, RandomTaggedMapperCreator::new));

    @Override
    public BlockMapper constructBlockMapper(Level world, double x, double y, double z, RandomSource random) {
        Integer[] metaTypes = new Integer[variantCount.getValue(random)];
        for (int i = 0; i < metaTypes.length; i++) {
            metaTypes[i] = random.nextInt(BuiltInRegistries.BLOCK.getTag(toReplace).map(HolderSet.ListBacked::size).orElse(32));
        }
        return new RandomTaggedMapper(Optional.empty(), toReplace, metaTypes);
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapperCreator> codec() {
        return CODEC;
    }
}
