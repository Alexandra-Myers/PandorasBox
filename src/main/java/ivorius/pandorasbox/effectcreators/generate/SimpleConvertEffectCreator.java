package ivorius.pandorasbox.effectcreators.generate;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effectcreators.generate.block_mappers.BlockMapperCreator;
import ivorius.pandorasbox.effectcreators.generate.feature_generators.FeatureGeneratorCreator;
import ivorius.pandorasbox.effects.generate.GenerateEffect;
import ivorius.pandorasbox.effects.generate.SimpleConvertEffect;
import ivorius.pandorasbox.effects.generate.entity_spawners.EntitySpawner;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static ivorius.pandorasbox.effects.generate.SimpleConvertEffect.NO_EXCLUSIONS;

public record SimpleConvertEffectCreator(Optional<ResourceKey<Biome>> optionalBiome, Either<Block, TagKey<Block>>[] excludedTargets, List<BlockMapperCreator> mappers,
                                         List<FeatureGeneratorCreator> generators, List<EntitySpawner> spawners) implements GenerateEffectCreator {
    public static final MapCodec<SimpleConvertEffectCreator> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(ResourceKey.codec(Registries.BIOME).optionalFieldOf("biome").forGetter(SimpleConvertEffectCreator::optionalBiome),
                            PBNBTHelper.arrayCodec(Codec.either(BuiltInRegistries.BLOCK.byNameCodec(), TagKey.hashedCodec(Registries.BLOCK)), () -> (Either<Block, TagKey<Block>>[]) new Either[0]).fieldOf("exclusions").forGetter(SimpleConvertEffectCreator::excludedTargets),
                            BlockMapperCreator.CODEC.listOf().fieldOf("mappers").forGetter(SimpleConvertEffectCreator::mappers),
                            FeatureGeneratorCreator.CODEC.listOf().fieldOf("generators").forGetter(SimpleConvertEffectCreator::generators),
                            EntitySpawner.CODEC.listOf().fieldOf("spawners").forGetter(SimpleConvertEffectCreator::spawners))
                    .apply(instance, SimpleConvertEffectCreator::new));
    public SimpleConvertEffectCreator(Optional<ResourceKey<Biome>> optionalBiome, List<BlockMapperCreator> mappers, List<FeatureGeneratorCreator> generators, List<EntitySpawner> spawners) {
        this(optionalBiome, NO_EXCLUSIONS, mappers, generators, spawners);
    }

    @Override
    public GenerateEffect constructGenerate(Level world, double x, double y, double z, RandomSource random) {
        return new SimpleConvertEffect(optionalBiome, excludedTargets, mappers.stream().map(blockMapperCreator -> blockMapperCreator.constructBlockMapper(world, x, y, z, random)).toList(), generators.stream().map(featureGeneratorCreator -> featureGeneratorCreator.constructFeatureGenerator(world, x, y, z, random)).toList(), spawners);
    }

    @Override
    public int getPasses() {
        int passes = 1;
        if (!spawners().isEmpty()) passes = 3;
        else if (!generators().isEmpty()) passes = 2;
        return passes;
    }

    @Override
    public @NotNull MapCodec<? extends GenerateEffectCreator> codec() {
        return CODEC;
    }
}
