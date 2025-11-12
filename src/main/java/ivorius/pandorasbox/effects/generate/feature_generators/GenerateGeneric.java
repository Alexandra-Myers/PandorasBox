package ivorius.pandorasbox.effects.generate.feature_generators;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import ivorius.pandorasbox.weighted.WeightedResourceKey;
import ivorius.pandorasbox.weighted.WeightedSelector;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static ivorius.pandorasbox.effects.PBEffect.isBlockAnyOf;
import static ivorius.pandorasbox.effects.PBEffect.setBlockSafe;

public record GenerateGeneric(double baseChance, Either<Block, TagKey<Block>>[] set, List<WeightedResourceKey<ConfiguredFeature<?, ?>>> featureSet, Block placeBlock,
                              boolean force) implements FeatureGenerator {
    @SuppressWarnings("unchecked")
    public static final MapCodec<GenerateGeneric> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.DOUBLE.fieldOf("base_chance").forGetter(GenerateGeneric::baseChance),
                            PBNBTHelper.arrayCodec(Codec.either(BuiltInRegistries.BLOCK.byNameCodec(), TagKey.hashedCodec(Registries.BLOCK)), () -> (Either<Block, TagKey<Block>>[]) new Either[0]).fieldOf("block_set").forGetter(GenerateGeneric::set),
                            WeightedResourceKey.codec(Registries.CONFIGURED_FEATURE).listOf().fieldOf("feature_set").forGetter(GenerateGeneric::featureSet),
                            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("placed").forGetter(GenerateGeneric::placeBlock),
                            Codec.BOOL.fieldOf("force").forGetter(GenerateGeneric::force))
                    .apply(instance, GenerateGeneric::new));
    public GenerateGeneric(double baseChance, Either<Block, TagKey<Block>>[] exclusions, ResourceKey<ConfiguredFeature<?, ?>> feature, Block placeBlock) {
        this(baseChance, exclusions, List.of(new WeightedResourceKey<>(feature)), placeBlock, false);
    }
    @Override
    public void finalGenerate(ServerLevel serverLevel, BlockPos pos, BlockState blockState, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        BlockPos posBelow = pos.below();
        BlockState blockBelowState = serverLevel.getBlockState(posBelow);

        if (serverLevel.getBlockState(pos).isAir() && (isBlockAnyOf(blockBelowState.getBlock(), set) == force) && blockBelowState.isRedstoneConductor(serverLevel, posBelow)) {
            setBlockSafe(serverLevel, posBelow, placeBlock.defaultBlockState());
            Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = serverLevel.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);
            configuredFeatureRegistry.getOrThrow(WeightedSelector.selectItem(random, featureSet).key()).place(serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos);
        }
    }

    @Override
    public @NotNull MapCodec<? extends FeatureGenerator> codec() {
        return CODEC;
    }
}
