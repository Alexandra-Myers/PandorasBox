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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static ivorius.pandorasbox.effects.PBEffect.isBlockAnyOf;
import static ivorius.pandorasbox.effects.PBEffect.setBlockSafe;

public record SurfaceMapper(Either<Block, TagKey<Block>>[] targets, Block surfaceBlock, Block underBlock, double discardSurfaceChance) implements BlockMapper {
    @SuppressWarnings({"unchecked", "RedundantCast"})
    public static final MapCodec<SurfaceMapper> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(Codec.either(BuiltInRegistries.BLOCK.byNameCodec(), TagKey.hashedCodec(Registries.BLOCK)), () -> (Either<Block, TagKey<Block>>[]) new Either[0]).fieldOf("targets").forGetter(SurfaceMapper::targets),
                            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("surface_block").forGetter(SurfaceMapper::surfaceBlock),
                            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("under_block").forGetter(SurfaceMapper::underBlock),
                            Codec.DOUBLE.fieldOf("discard_surface_chance").forGetter(SurfaceMapper::discardSurfaceChance))
                    .apply(instance, SurfaceMapper::new));

    @Override
    public boolean matches(ServerLevel serverLevel, PandorasBoxEntity entity, BlockPos blockPos, BlockState state, RandomSource random) {
        return isBlockAnyOf(state.getBlock(), targets);
    }

    @Override
    public void convertBlock(ServerLevel serverLevel, BlockPos blockPos, BlockState state, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        BlockState blockAboveState = serverLevel.getBlockState(blockPos.above());
        if ((blockAboveState.isAir() || blockAboveState.canBeReplaced()) && random.nextDouble() > discardSurfaceChance) setBlockSafe(serverLevel, blockPos, PandorasBoxHelper.getRandomBlockState(random, surfaceBlock, unifiedSeed));
        else setBlockSafe(serverLevel, blockPos, PandorasBoxHelper.getRandomBlockState(random, underBlock, unifiedSeed));
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapper> codec() {
        return CODEC;
    }
}
