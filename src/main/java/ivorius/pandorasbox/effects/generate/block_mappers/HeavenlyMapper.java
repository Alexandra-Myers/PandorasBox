package ivorius.pandorasbox.effects.generate.block_mappers;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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

import static ivorius.pandorasbox.effects.PBEffect.isBlockAnyOf;
import static ivorius.pandorasbox.effects.PBEffect.setBlockSafe;

public record HeavenlyMapper(Either<Block, TagKey<Block>>[] targets) implements BlockMapper {
    public static final MapCodec<HeavenlyMapper> CODEC = PBNBTHelper.arrayCodec(Codec.either(BuiltInRegistries.BLOCK.byNameCodec(), TagKey.hashedCodec(Registries.BLOCK)), () -> (Either<Block, TagKey<Block>>[]) new Either[0]).fieldOf("targets").xmap(HeavenlyMapper::new, HeavenlyMapper::targets);
    @Override
    public boolean matches(ServerLevel serverLevel, PandorasBoxEntity entity, BlockPos blockPos, BlockState state, RandomSource random) {
        return targets.length == 0 || isBlockAnyOf(state.getBlock(), targets);
    }

    @Override
    public void convertBlock(ServerLevel serverLevel, BlockPos pos, BlockState state, RandomSource random, PandorasBoxEntity entity, Vec3 effectCenter, int pass, int unifiedSeed, double range) {
        if (serverLevel.getBlockState(pos.above()).getBlock() == Blocks.AIR) {
            if (serverLevel.random.nextInt(6 * 6) == 0) {
                setBlockSafe(serverLevel, pos, Blocks.DIRT.defaultBlockState());
                setBlockSafe(serverLevel, pos.above(), Blocks.OAK_LOG.defaultBlockState());
                setBlockSafe(serverLevel, pos.above(2), Blocks.OAK_LEAVES.defaultBlockState());
            } else if (serverLevel.random.nextInt(6 * 6) == 0) {
                int pHeight = random.nextInt(5) + 3;
                for (int yp = 0; yp < pHeight; yp++)
                    setBlockSafe(serverLevel, pos.above(yp), Blocks.QUARTZ_BLOCK.defaultBlockState());
            } else if (serverLevel.random.nextInt(2 * 2) == 0) {
                setBlockSafe(serverLevel, pos, Blocks.GLASS.defaultBlockState());
            } else if (serverLevel.random.nextInt(8 * 8) == 0) {
                setBlockSafe(serverLevel, pos, Blocks.GLASS.defaultBlockState());
                setBlockSafe(serverLevel, pos.below(), Blocks.REDSTONE_LAMP.defaultBlockState());
                setBlockSafe(serverLevel, pos.below(2), Blocks.REDSTONE_BLOCK.defaultBlockState());
            } else
                setBlockSafe(serverLevel, pos, Blocks.STONE_BRICKS.defaultBlockState());
        } else {
            setBlockSafe(serverLevel, pos, Blocks.STONE_BRICKS.defaultBlockState());
        }
    }

    @Override
    public @NotNull MapCodec<? extends BlockMapper> codec() {
        return CODEC;
    }
}
