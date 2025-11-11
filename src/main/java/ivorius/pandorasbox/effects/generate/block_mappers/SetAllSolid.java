package ivorius.pandorasbox.effects.generate.block_mappers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static ivorius.pandorasbox.effects.PBEffect.setBlockSafe;

public record SetAllSolid(Block toReplace, Optional<BlockMapper> otherwise) implements BlockMapper {
    public static final MapCodec<SetAllSolid> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("to_replace").forGetter(SetAllSolid::toReplace),
                            BlockMapper.CODEC.optionalFieldOf("otherwise").forGetter(SetAllSolid::otherwise))
                    .apply(instance, SetAllSolid::new));

    @Override
    public boolean matches(ServerLevel serverLevel, PandorasBoxEntity entity, BlockPos blockPos, BlockState state, RandomSource random) {
        return otherwise.map(blockMapper -> blockMapper.matches(serverLevel, entity, blockPos, state, random) && state.isCollisionShapeFullBlock(serverLevel, blockPos)).orElseGet(() -> state.isCollisionShapeFullBlock(serverLevel, blockPos));
    }

    @Override
    public void convertBlock(ServerLevel serverLevel, BlockPos blockPos, BlockState state, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        if (otherwise.isEmpty()) setBlockSafe(serverLevel, blockPos, PandorasBoxHelper.getRandomBlockState(random, toReplace, unifiedSeed));
        else otherwise.get().convertBlock(serverLevel, blockPos, state, random, entity, effectCenter, pass, unifiedSeed, range);
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapper> codec() {
        return CODEC;
    }
}
