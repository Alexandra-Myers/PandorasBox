package ivorius.pandorasbox.effects.generate;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.generate.block_mappers.BlockMapper;
import ivorius.pandorasbox.effects.generate.entity_spawners.EntitySpawner;
import ivorius.pandorasbox.effects.generate.feature_generators.FeatureGenerator;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record SimpleConvertEffect(Optional<ResourceKey<Biome>> optionalBiome, Either<Block, TagKey<Block>>[] excludedTargets, List<BlockMapper> mappers,
                                  List<FeatureGenerator> generators, List<EntitySpawner> spawners) implements GenerateConvertEffect {
    public static final Either<Block, TagKey<Block>>[] NO_EXCLUSIONS = new Either[0];
    public static final MapCodec<SimpleConvertEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(ResourceKey.codec(Registries.BIOME).optionalFieldOf("biome").forGetter(SimpleConvertEffect::optionalBiome),
                            PBNBTHelper.arrayCodec(Codec.either(BuiltInRegistries.BLOCK.byNameCodec(), TagKey.hashedCodec(Registries.BLOCK)), () -> (Either<Block, TagKey<Block>>[]) new Either[0]).fieldOf("exclusions").forGetter(SimpleConvertEffect::excludedTargets),
                            BlockMapper.CODEC.listOf().fieldOf("mappers").forGetter(SimpleConvertEffect::mappers),
                            FeatureGenerator.CODEC.listOf().fieldOf("generators").forGetter(SimpleConvertEffect::generators),
                            EntitySpawner.CODEC.listOf().fieldOf("spawners").forGetter(SimpleConvertEffect::spawners))
                    .apply(instance, SimpleConvertEffect::new));
    public SimpleConvertEffect(Optional<ResourceKey<Biome>> optionalBiome, List<BlockMapper> mappers, List<FeatureGenerator> generators, List<EntitySpawner> spawners) {
        this(optionalBiome, NO_EXCLUSIONS, mappers, generators, spawners);
    }
    @Override
    public void runPostConvert(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range, int unifiedSeed) {

    }

    @Override
    public @Nullable ResourceKey<Biome> biome() {
        return optionalBiome.orElse(null);
    }

    @Override
    public @NotNull MapCodec<? extends GenerateEffect> codec() {
        return CODEC;
    }
}
