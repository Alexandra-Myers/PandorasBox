package ivorius.pandorasbox.effects.generate;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.generate.block_mappers.BlockMapper;
import ivorius.pandorasbox.effects.generate.entity_spawners.EntitySpawner;
import ivorius.pandorasbox.effects.generate.feature_generators.FeatureGenerator;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.atlas.atlascore.AtlasCore;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record SimpleConvertEffect(Optional<ResourceKey<Biome>> optionalBiome, Either<Block, TagKey<Block>>[] excludedTargets, List<BlockMapper> mappers,
                                  List<FeatureGenerator> generators, List<EntitySpawner> spawners, RegistryAccess access) implements GenerateConvertEffect {
    public static final Either<Block, TagKey<Block>>[] NO_EXCLUSIONS = new Either[0];
    public static final MapCodec<SimpleConvertEffect> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(ResourceKey.codec(Registries.BIOME).optionalFieldOf("biome").forGetter(SimpleConvertEffect::optionalBiome),
                            PBNBTHelper.arrayCodec(Codec.either(BuiltInRegistries.BLOCK.byNameCodec(), TagKey.hashedCodec(Registries.BLOCK)), () -> (Either<Block, TagKey<Block>>[]) new Either[0]).fieldOf("exclusions").forGetter(SimpleConvertEffect::excludedTargets),
                            BlockMapper.CODEC.listOf().fieldOf("mappers").forGetter(SimpleConvertEffect::mappers),
                            FeatureGenerator.CODEC.listOf().fieldOf("generators").forGetter(SimpleConvertEffect::generators),
                            EntitySpawner.CODEC.listOf().fieldOf("spawners").forGetter(SimpleConvertEffect::spawners))
                    .apply(instance, SimpleConvertEffect::new));
    public SimpleConvertEffect(Optional<ResourceKey<Biome>> optionalBiome, List<BlockMapper> mappers, List<FeatureGenerator> generators, List<EntitySpawner> spawners, RegistryAccess access) {
        this(optionalBiome, NO_EXCLUSIONS, mappers, generators, spawners, access);
    }
    public SimpleConvertEffect(Optional<ResourceKey<Biome>> optionalBiome, Either<Block, TagKey<Block>>[] excludedTargets, List<BlockMapper> mappers,
                               List<FeatureGenerator> generators, List<EntitySpawner> spawners, RegistryAccess access) {
        this.optionalBiome = optionalBiome;
        this.excludedTargets = excludedTargets;
        this.mappers = mappers;
        this.generators = generators;
        this.spawners = spawners;
        this.access = access;
        if (access == null) return;
        Path pathLoc = Path.of(FabricLoader.getInstance().getGameDir().toAbsolutePath() + "/converts/");
        if (!Files.exists(pathLoc)) {
            try {
                Files.createDirectories(pathLoc);
            } catch (IOException e) {
                return;
            }
        }
        File loc = new File(pathLoc.toAbsolutePath() + "/" + hashCode() + ".json");
        if (!loc.exists()) {
            try {
                loc.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            PrintWriter printWriter = new PrintWriter(loc);
            JsonElement inst = CODEC.codec().encodeStart(RegistryOps.create(JsonOps.INSTANCE, access), this).getOrThrow();
            AtlasCore.GSON.toJson(inst, printWriter);
            printWriter.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public SimpleConvertEffect(Optional<ResourceKey<Biome>> biomeResourceKey, Either<Block, TagKey<Block>>[] eithers, List<BlockMapper> mappers, List<FeatureGenerator> featureGenerators, List<EntitySpawner> spawners) {
        this(biomeResourceKey, mappers, featureGenerators, spawners, null);
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimpleConvertEffect that)) return false;
        return Objects.equals(mappers(), that.mappers()) && Objects.equals(spawners(), that.spawners()) && Objects.equals(generators(), that.generators()) && Objects.equals(optionalBiome(), that.optionalBiome()) && Objects.deepEquals(excludedTargets(), that.excludedTargets());
    }

    @Override
    public int hashCode() {
        return Objects.hash(optionalBiome(), Arrays.hashCode(excludedTargets()), mappers(), generators(), spawners());
    }
}
