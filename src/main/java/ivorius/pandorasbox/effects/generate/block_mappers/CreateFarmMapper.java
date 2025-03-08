package ivorius.pandorasbox.effects.generate.block_mappers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static ivorius.pandorasbox.effects.PBEffect.setBlockSafe;

public record CreateFarmMapper(double cropChance) implements BlockMapper {
    public static final MapCodec<CreateFarmMapper> CODEC = Codec.DOUBLE.xmap(CreateFarmMapper::new, CreateFarmMapper::cropChance).fieldOf("crop_chance");
    @Override
    public boolean matches(ServerLevel serverLevel, PandorasBoxEntity entity, BlockPos blockPos, BlockState state, RandomSource random) {
        return serverLevel.getBlockState(blockPos.below()).isRedstoneConductor(serverLevel, blockPos.below()) && state.isAir() && state.getBlock() != Blocks.WATER && random.nextDouble() < cropChance;
    }

    @Override
    public void convertBlock(ServerLevel serverLevel, BlockPos pos, BlockState state, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        BlockPos posBelow = pos.below();
        int b = serverLevel.random.nextInt(7);

        if (b == 0) {
            setBlockSafe(serverLevel, posBelow, Blocks.FARMLAND.defaultBlockState());
            setBlockSafe(serverLevel, pos, Blocks.PUMPKIN_STEM.getStateDefinition().getPossibleStates().get(serverLevel.random.nextInt(4) + 4));
        } else if (b == 1) {
            setBlockSafe(serverLevel, posBelow, Blocks.FARMLAND.defaultBlockState());
            setBlockSafe(serverLevel, pos, Blocks.MELON_STEM.getStateDefinition().getPossibleStates().get(serverLevel.random.nextInt(4) + 4));
        } else if (b == 2) {
            setBlockSafe(serverLevel, posBelow, Blocks.FARMLAND.defaultBlockState());
            setBlockSafe(serverLevel, pos, Blocks.WHEAT.getStateDefinition().getPossibleStates().get(serverLevel.random.nextInt(8)));
        } else if (b == 3) {
            setBlockSafe(serverLevel, posBelow, Blocks.FARMLAND.defaultBlockState());
            setBlockSafe(serverLevel, pos, Blocks.CARROTS.getStateDefinition().getPossibleStates().get(serverLevel.random.nextInt(8)));
        } else if (b == 4) {
            setBlockSafe(serverLevel, posBelow, Blocks.FARMLAND.defaultBlockState());
            setBlockSafe(serverLevel, pos, Blocks.POTATOES.getStateDefinition().getPossibleStates().get(serverLevel.random.nextInt(8)));
        } else if (b == 5) {
            setBlockSafe(serverLevel, pos, Blocks.HAY_BLOCK.defaultBlockState());
        } else {
            setBlockSafe(serverLevel, posBelow, Blocks.WATER.defaultBlockState());
        }
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapper> codec() {
        return CODEC;
    }
}
