package ivorius.pandorasbox.effects.generate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.extension.TreeFeatureExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record GenTreesOddEffect(boolean requiresSolidGround, double chancePerBlock, int generatorFlags, Block trunkBlock, Block leavesBlock, List<ResourceKey<ConfiguredFeature<?, ?>>> generators) implements GenerateByGeneratorEffect<ResourceKey<ConfiguredFeature<?, ?>>> {
    public static final MapCodec<GenTreesOddEffect> CODEC = RecordCodecBuilder.mapCodec(aInstance ->
            aInstance.group(Codec.BOOL.fieldOf("requires_solid_ground").forGetter(GenTreesOddEffect::requiresSolidGround),
                            Codec.DOUBLE.fieldOf("chance_per_block").forGetter(GenTreesOddEffect::chancePerBlock),
                            Codec.INT.fieldOf("generator_flags").forGetter(GenTreesOddEffect::generatorFlags),
                            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("trunk").forGetter(GenTreesOddEffect::trunkBlock),
                            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("leaves").forGetter(GenTreesOddEffect::leavesBlock),
                            ResourceKey.codec(Registries.CONFIGURED_FEATURE).listOf().fieldOf("features").forGetter(GenTreesOddEffect::generators))
                    .apply(aInstance, GenTreesOddEffect::new));
    @Override
    public @Nullable ResourceKey<Biome> biome() {
        return null;
    }

    @Override
    public @NotNull MapCodec<? extends GenerateEffect> codec() {
        return CODEC;
    }

    @Override
    public void generateGenerator(ResourceKey<ConfiguredFeature<?, ?>> generator, ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos) {
        Optional<Registry<ConfiguredFeature<?, ?>>> configuredFeatureRegistry = serverLevel.registryAccess().lookup(Registries.CONFIGURED_FEATURE);
        if (configuredFeatureRegistry.isEmpty()) return;
        ConfiguredFeature<?, ?> feature = configuredFeatureRegistry.get().getValueOrThrow(generator);
        TreeFeatureExtensions.placeWithBlockOverrides(feature, serverLevel, serverLevel.getChunkSource().getGenerator(), randomSource, blockPos, trunkBlock.defaultBlockState(), leavesBlock.defaultBlockState(), null);
    }
}
