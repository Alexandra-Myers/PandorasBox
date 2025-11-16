package ivorius.pandorasbox.effects.generate.block_mappers;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static ivorius.pandorasbox.effects.PBEffect.setBlockSafe;
import static net.minecraft.util.ExtraCodecs.validate;

public record LavaChillMapper(Block fluidTarget, Block toReplace) implements BlockMapper {
    public static final MapCodec<LavaChillMapper> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(validate(BuiltInRegistries.BLOCK.byNameCodec(), block -> block.defaultBlockState().hasProperty(LiquidBlock.LEVEL) ? DataResult.success(block) : DataResult.error(() -> "Not a fluid!")).fieldOf("fluid_target").forGetter(LavaChillMapper::fluidTarget),
                                BuiltInRegistries.BLOCK.byNameCodec().fieldOf("to_replace").forGetter(LavaChillMapper::toReplace))
                        .apply(instance, LavaChillMapper::new));
    @Override
    public boolean matches(ServerLevel serverLevel, PandorasBoxEntity entity, BlockPos blockPos, BlockState state, RandomSource random) {
        return (state.getBlock() == fluidTarget && !state.getValue(LiquidBlock.LEVEL).equals(0));
    }

    @Override
    public void convertBlock(ServerLevel serverLevel, BlockPos blockPos, BlockState state, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        BlockState resState = toReplace.defaultBlockState();
        if (resState.hasProperty(LiquidBlock.LEVEL) && resState.hasProperty(LiquidBlock.LEVEL)) resState.setValue(LiquidBlock.LEVEL, state.getValue(LiquidBlock.LEVEL));
        setBlockSafe(serverLevel, blockPos, resState);
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapper> codec() {
        return CODEC;
    }
}
