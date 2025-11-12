package ivorius.pandorasbox.effects.generate.block_mappers;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static ivorius.pandorasbox.effects.PBEffect.isBlockAnyOf;
import static ivorius.pandorasbox.effects.PBEffect.setBlockSafe;

public record RandomTaggedMapper(Optional<Either<Block, TagKey<Block>>[]> targets, TagKey<Block> toReplace, Integer[] groundMetas) implements BlockMapper {
    @SuppressWarnings({"unchecked", "RedundantCast"})
    public static final MapCodec<RandomTaggedMapper> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(Codec.either(BuiltInRegistries.BLOCK.byNameCodec(), TagKey.hashedCodec(Registries.BLOCK)), () -> (Either<Block, TagKey<Block>>[]) new Either[0]).optionalFieldOf("targets").forGetter(RandomTaggedMapper::targets),
                            TagKey.codec(Registries.BLOCK).fieldOf("to_replace").forGetter(RandomTaggedMapper::toReplace),
                            PBNBTHelper.arrayCodec(Codec.INT, () -> new Integer[0]).fieldOf("ground_metas").forGetter(RandomTaggedMapper::groundMetas))
                    .apply(instance, RandomTaggedMapper::new));

    @Override
    public boolean matches(ServerLevel serverLevel, PandorasBoxEntity entity, BlockPos blockPos, BlockState state, RandomSource random) {
        return targets.isEmpty() || isBlockAnyOf(state.getBlock(), targets.get());
    }

    @Override
    public void convertBlock(ServerLevel serverLevel, BlockPos blockPos, BlockState state, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        HolderSet.Named<Block> terracotta = BuiltInRegistries.BLOCK.getOrThrow(toReplace);
        Block placeBlock = terracotta.get(groundMetas[random.nextInt(groundMetas.length)] % terracotta.size()).value();
        setBlockSafe(serverLevel, blockPos, PandorasBoxHelper.getRandomBlockState(random, placeBlock, unifiedSeed));
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapper> codec() {
        return CODEC;
    }
}
