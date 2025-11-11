package ivorius.pandorasbox.effects.generate.flags;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static ivorius.pandorasbox.effects.PBEffect.setBlockUnsafeSrc;

public record GenCover(int[] flags, boolean overSurface, Block[] blocks) implements GenerateByFlag {
    public static final MapCodec<GenCover> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(Codec.INT, () -> new Integer[0]).fieldOf("flags").forGetter(genCover -> Arrays.stream(genCover.flags).boxed().toArray(Integer[]::new)),
                            Codec.BOOL.fieldOf("over_surface").forGetter(GenCover::overSurface),
                            PBNBTHelper.arrayCodec(BuiltInRegistries.BLOCK.byNameCodec(), () -> new Block[0]).fieldOf("blocks").forGetter(GenCover::blocks))
                    .apply(instance, GenCover::new));
    private GenCover(Integer[] flags, boolean overSurface, Block[] blocks) {
        this(Arrays.stream(flags).mapToInt(Integer::intValue).toArray(), overSurface, blocks);
    }
    public GenCover(boolean overSurface, Block[] blocks) {
        this(new int[31 * 31], overSurface, blocks);
    }
    @Override
    public boolean hasFlag(Level world, PandorasBoxEntity entity, RandomSource random, BlockPos pos) {
        if (overSurface) {
            if (!isReplacable(world, pos)) {
                return false;
            }

            return isFullBlock(world, pos.west()) || isFullBlock(world, pos.east())
                    || isFullBlock(world, pos.below()) || isFullBlock(world, pos.above())
                    || isFullBlock(world, pos.north()) || isFullBlock(world, pos.south());
        } else {
            return isReplacable(world, pos.west()) || isReplacable(world, pos.east())
                    || isReplacable(world, pos.below()) || isReplacable(world, pos.above())
                    || isReplacable(world, pos.north()) || isReplacable(world, pos.south());
        }
    }

    private boolean isReplacable(Level world, BlockPos pos) {
        return world.getBlockState(pos).canSurvive(world, pos);
    }

    private boolean isFullBlock(Level world, BlockPos pos) {
        return world.getBlockState(pos).isCollisionShapeFullBlock(world, pos);
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, RandomSource random, int pass, int unifiedSeed, BlockPos pos, double range, boolean flag) {
        if (flag && !world.isClientSide()) {
            Block newBlock = blocks[random.nextInt(blocks.length)];
            setBlockUnsafeSrc(world, pos, PandorasBoxHelper.getRandomBlockState(random, newBlock, unifiedSeed));
        }
    }

    @Override
    public @NotNull MapCodec<? extends GenerateByFlag> codec() {
        return CODEC;
    }
}
