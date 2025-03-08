package ivorius.pandorasbox.effects.generate.block_mappers;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static ivorius.pandorasbox.effects.PBEffect.setBlockToAirSafe;
import static net.minecraft.world.level.block.Block.dropResources;

public record DryMapper() implements BlockMapper {
    public static final MapCodec<DryMapper> CODEC = MapCodec.unit(DryMapper::new);
    @Override
    public boolean matches(ServerLevel serverLevel, PandorasBoxEntity entity, BlockPos pos, BlockState state, RandomSource random) {
        FluidState fluidstate = serverLevel.getFluidState(pos);
        return fluidstate.is(FluidTags.WATER);
    }

    @Override
    public void convertBlock(ServerLevel serverLevel, BlockPos pos, BlockState state, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        Block block = state.getBlock();
        if (block instanceof BucketPickup bucketPickup && !bucketPickup.pickupBlock(null, serverLevel, pos, state).isEmpty()) return;
        if (!(state.getBlock() instanceof LiquidBlock)) {
            if (!state.is(Blocks.KELP) && !state.is(Blocks.KELP_PLANT) && !state.is(Blocks.SEAGRASS) && !state.is(Blocks.TALL_SEAGRASS)) return;

            BlockEntity blockEntity = state.hasBlockEntity() ? serverLevel.getBlockEntity(pos) : null;
            dropResources(state, serverLevel, pos, blockEntity);
        }
        setBlockToAirSafe(serverLevel, pos);
        for (Direction direction : Direction.values()) {
            BlockPos pos1 = pos.relative(direction);
                BlockState state1 = serverLevel.getBlockState(pos1);
                Block block1 = state1.getBlock();
                FluidState fluidState = serverLevel.getFluidState(pos1);
                if (fluidState.is(FluidTags.WATER)) {
                    if (block1 instanceof BucketPickup bucketPickup && !bucketPickup.pickupBlock(null, serverLevel, pos1, state1).isEmpty())
                        return;

                    if (!(state1.getBlock() instanceof LiquidBlock)) {
                        if (!state1.is(Blocks.KELP) && !state1.is(Blocks.KELP_PLANT) && !state1.is(Blocks.SEAGRASS) && !state1.is(Blocks.TALL_SEAGRASS))
                            return;

                        BlockEntity blockEntity = state1.hasBlockEntity() ? serverLevel.getBlockEntity(pos1) : null;
                        dropResources(state1, serverLevel, pos1, blockEntity);
                    }
                    setBlockToAirSafe(serverLevel, pos1);
                }
        }
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapper> codec() {
        return CODEC;
    }
}
