package ivorius.pandorasbox.effects.generate.block_mappers;

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

public record IceMapper() implements BlockMapper {
    public static final MapCodec<IceMapper> CODEC = MapCodec.unit(IceMapper::new);
    @Override
    public boolean matches(ServerLevel serverLevel, PandorasBoxEntity entity, BlockPos blockPos, BlockState state, RandomSource random) {
        return serverLevel.loadedAndEntityCanStandOn(blockPos, entity);
    }

    @Override
    public void convertBlock(ServerLevel serverLevel, BlockPos pos, BlockState state, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        int mode = random.nextInt(6);

        if (mode == 0)
            setBlockSafe(serverLevel, pos, Blocks.ICE.defaultBlockState());
        else if (mode == 1)
            setBlockSafe(serverLevel, pos, Blocks.PACKED_ICE.defaultBlockState());
        else if (mode == 2)
            setBlockSafe(serverLevel, pos, Blocks.BLUE_ICE.defaultBlockState());
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapper> codec() {
        return CODEC;
    }
}
