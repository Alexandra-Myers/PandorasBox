package ivorius.pandorasbox.effects.generate.block_mappers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.Init;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public interface BlockMapper {
    Codec<BlockMapper> CODEC = Init.BLOCK_MAPPER_TYPE_REGISTRY.byNameCodec()
            .dispatch(BlockMapper::codec, Function.identity());
    MapCodec<BlockMapper> MAP_CODEC = Init.BLOCK_MAPPER_TYPE_REGISTRY.byNameCodec()
            .dispatchMap("mapper_type", BlockMapper::codec, Function.identity());
    default boolean convert(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range, int unifiedSeed) {
        if (world instanceof ServerLevel serverLevel && matches(serverLevel, entity, pos, serverLevel.getBlockState(pos), random)) return convertBlockRet(serverLevel, pos, serverLevel.getBlockState(pos), random, entity, effectCenter, pass, unifiedSeed, range);
        return true;
    }
    boolean matches(ServerLevel serverLevel, PandorasBoxEntity entity, BlockPos blockPos, BlockState state, RandomSource random);
    default boolean convertBlockRet(ServerLevel serverLevel, BlockPos blockPos, BlockState state, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        convertBlock(serverLevel, blockPos, state, random, entity, effectCenter, pass, unifiedSeed, range);
        return false;
    }
    void convertBlock(ServerLevel serverLevel, BlockPos blockPos, BlockState state, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range);
    @NotNull MapCodec<? extends BlockMapper> codec();
}
