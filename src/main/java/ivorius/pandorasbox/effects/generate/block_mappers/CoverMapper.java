package ivorius.pandorasbox.effects.generate.block_mappers;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static ivorius.pandorasbox.effects.PBEffect.setBlockSafe;

public record CoverMapper(Block block) implements BlockMapper {
    public static final MapCodec<CoverMapper> CODEC = BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").xmap(CoverMapper::new, CoverMapper::block);
    @Override
    public boolean matches(ServerLevel serverLevel, PandorasBoxEntity entity, BlockPos blockPos, BlockState state, RandomSource random) {
        return serverLevel.getBlockState(blockPos.below()).isCollisionShapeFullBlock(serverLevel, blockPos.below()) && state.isAir() && block.defaultBlockState().canSurvive(serverLevel, blockPos);
    }

    @Override
    public void convertBlock(ServerLevel serverLevel, BlockPos pos, BlockState state, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        setBlockSafe(serverLevel, pos, PandorasBoxHelper.getRandomBlockState(random, Blocks.SNOW, unifiedSeed));
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapper> codec() {
        return CODEC;
    }
}
