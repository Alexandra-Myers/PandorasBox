package ivorius.pandorasbox.effects.generate.block_mappers;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static ivorius.pandorasbox.effects.PBEffect.*;

public record SimpleConvertMapper(Either<Block, TagKey<Block>>[] targets, Block toReplace) implements BlockMapper {
    @SuppressWarnings({"unchecked", "RedundantCast"})
    public static final MapCodec<SimpleConvertMapper> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(Codec.either(BuiltInRegistries.BLOCK.byNameCodec(), TagKey.hashedCodec(Registries.BLOCK)), () -> (Either<Block, TagKey<Block>>[]) new Either[0]).fieldOf("targets").forGetter(SimpleConvertMapper::targets),
                            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("to_replace").forGetter(SimpleConvertMapper::toReplace))
                    .apply(instance, SimpleConvertMapper::new));

    @Override
    public boolean matches(ServerLevel serverLevel, PandorasBoxEntity entity, BlockPos blockPos, BlockState state, RandomSource random) {
        if (toReplace == Blocks.AIR) return targets.length == 0 || isBlockAnyOf(state.getBlock(), targets);
        return (targets.length == 0 || isBlockAnyOf(state.getBlock(), targets)) && !isBlockAnyOf(state.getBlock(), Either.left(toReplace));
    }

    @Override
    public void convertBlock(ServerLevel serverLevel, BlockPos blockPos, BlockState state, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        if (toReplace == Blocks.AIR) setBlockToAirSafe(serverLevel, blockPos);
        else setBlockSafe(serverLevel, blockPos, PandorasBoxHelper.getRandomBlockState(random, toReplace, unifiedSeed));
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapper> codec() {
        return CODEC;
    }
}
