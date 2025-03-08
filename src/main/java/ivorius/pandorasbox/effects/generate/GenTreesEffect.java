package ivorius.pandorasbox.effects.generate;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record GenTreesEffect(boolean requiresSolidGround, double chancePerBlock, int generatorFlags, List<ResourceKey<ConfiguredFeature<?, ?>>> generators) implements GenerateByGeneratorEffect<ResourceKey<ConfiguredFeature<?, ?>>> {
    public static final MapCodec<GenTreesEffect> CODEC = GenerateByGeneratorEffect.prepareCodec(instance -> ResourceKey.codec(Registries.CONFIGURED_FEATURE).listOf().fieldOf("features").forGetter(GenTreesEffect::generators), GenTreesEffect::new);
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
        feature.place(serverLevel, serverLevel.getChunkSource().getGenerator(), randomSource, blockPos);
    }
}
