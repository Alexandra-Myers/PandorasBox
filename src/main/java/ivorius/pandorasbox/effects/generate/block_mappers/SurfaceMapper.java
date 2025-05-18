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
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static ivorius.pandorasbox.effects.PBEffect.*;

public record SurfaceMapper(Either<Block, TagKey<Block>>[] targets, Block surfaceBlock, Block underBlock, double discardSurfaceChance, int maxBlocksBelowSurface) implements BlockMapper {
    @SuppressWarnings({"unchecked", "RedundantCast"})
    public static final MapCodec<SurfaceMapper> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(Codec.either(BuiltInRegistries.BLOCK.byNameCodec(), TagKey.hashedCodec(Registries.BLOCK)), () -> (Either<Block, TagKey<Block>>[]) new Either[0]).fieldOf("targets").forGetter(SurfaceMapper::targets),
                            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("surface_block").forGetter(SurfaceMapper::surfaceBlock),
                            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("under_block").forGetter(SurfaceMapper::underBlock),
                            Codec.DOUBLE.fieldOf("discard_surface_chance").forGetter(SurfaceMapper::discardSurfaceChance),
                            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_blocks_beneath_surface").forGetter(SurfaceMapper::maxBlocksBelowSurface))
                    .apply(instance, SurfaceMapper::new));
    @SuppressWarnings({"unchecked", "RedundantCast"})
    public static final MapCodec<SurfaceMapper> EXTRA_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(Codec.either(BuiltInRegistries.BLOCK.byNameCodec(), TagKey.hashedCodec(Registries.BLOCK)), () -> (Either<Block, TagKey<Block>>[]) new Either[0]).fieldOf("targets").forGetter(SurfaceMapper::targets),
                            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("to_replace").forGetter(SurfaceMapper::surfaceBlock),
                            ExtraCodecs.POSITIVE_INT.fieldOf("max_blocks_beneath_surface").forGetter(SurfaceMapper::maxBlocksBelowSurface))
                    .apply(instance, SurfaceMapper::new));
    public SurfaceMapper(Either<Block, TagKey<Block>>[] targets, Block toReplace, int maxBlocksBelowSurface) {
        this(targets, toReplace, toReplace, 0, maxBlocksBelowSurface);
    }

    @Override
    public boolean matches(ServerLevel serverLevel, PandorasBoxEntity entity, BlockPos blockPos, BlockState state, RandomSource random) {
        BlockState blockAboveState = serverLevel.getBlockState(blockPos.above(maxBlocksBelowSurface));
        return (targets.length == 0 || isBlockAnyOf(state.getBlock(), targets)) && (maxBlocksBelowSurface == 0 || blockAboveState.isAir() || blockAboveState.canBeReplaced() || !blockAboveState.isRedstoneConductor(serverLevel, blockPos.above()));
    }

    @Override
    public void convertBlock(ServerLevel serverLevel, BlockPos blockPos, BlockState state, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        if (surfaceBlock.equals(underBlock)) {
            if (underBlock == Blocks.AIR) setBlockToAirSafe(serverLevel, blockPos);
            else setBlockSafe(serverLevel, blockPos, PandorasBoxHelper.getRandomBlockState(random, underBlock, unifiedSeed));
            return;
        }
        BlockState blockAboveState = serverLevel.getBlockState(blockPos.above());
        if ((blockAboveState.isAir() || blockAboveState.canBeReplaced() || !blockAboveState.isRedstoneConductor(serverLevel, blockPos.above())) && random.nextDouble() > discardSurfaceChance) setBlockSafe(serverLevel, blockPos, PandorasBoxHelper.getRandomBlockState(random, surfaceBlock, unifiedSeed));
        else setBlockSafe(serverLevel, blockPos, PandorasBoxHelper.getRandomBlockState(random, underBlock, unifiedSeed));
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapper> codec() {
        return CODEC;
    }
}
