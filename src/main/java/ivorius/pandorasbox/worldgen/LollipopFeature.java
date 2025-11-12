package ivorius.pandorasbox.worldgen;

import com.mojang.serialization.Codec;
import ivorius.pandorasbox.random.ILinear;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.worldgen.configuration.LollipopConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.NotNull;

public class LollipopFeature extends Feature<@NotNull LollipopConfiguration> implements RestrictedColorFeature<LollipopConfiguration> {
    public static final IValue DEFAULT = new ILinear(1, 4);
    public LollipopFeature(Codec<LollipopConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<@NotNull LollipopConfiguration> featurePlaceContext) {
        return pandorasBox$placeWithRestrictedColors(featurePlaceContext, DEFAULT, Blocks.GRASS_BLOCK.defaultBlockState());
    }

    @Override
    public boolean pandorasBox$placeWithRestrictedColors(FeaturePlaceContext<@NotNull LollipopConfiguration> featurePlaceContext, IValue possibleColors, BlockState soil) {
        RandomSource rand = featurePlaceContext.random();
        WorldGenLevel world = featurePlaceContext.level();
        int size = featurePlaceContext.config().size().getValue(rand);
        HolderSet<Block> blocks = featurePlaceContext.config().blocks();
        int[] indexes = new int[possibleColors.getValue(rand)];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = rand.nextInt(blocks.size());
        }

        BlockPos position = featurePlaceContext.origin();

        boolean inWorldHeight = true;

        int posX = position.getX();
        int posY = position.getY();
        int posZ = position.getZ();

        if (posY >= world.getMinBuildHeight() && posY + size + 1 <= world.getMaxBuildHeight()) {
            for (int i = posY; i <= posY + 1 + size; ++i) {
                inWorldHeight &= i >= world.getMinBuildHeight() && i < world.getMaxBuildHeight();
            }

            if (!inWorldHeight) {
                return false;
            } else {
                boolean rotated = rand.nextBoolean();

                if (posY < world.getMaxBuildHeight() - size) {
                    int height;

                    for (int shift = -1; shift <= 1; shift++) {
                        for (int s = -size / 2; s <= size / 2; s++) {
                            for (int y = -size / 2; y <= size / 2; y++) {
                                if (s * s + y * y <= (size * size / 4 - shift * shift * 4)) {
                                    int x = (!rotated ? s : shift) + posX;
                                    int z = (rotated ? s : shift) + posZ;
                                    int rY = y + posY + size;

                                    BlockPos pos1 = new BlockPos(x, rY, z);
                                    BlockState block1State = world.getBlockState(pos1);

                                    if (block1State.isAir() || block1State.is(BlockTags.LEAVES)) {
                                        Block block = blocks.get(indexes[rand.nextInt(indexes.length)]).value();
                                        this.setBlock(world, pos1, block.defaultBlockState());
                                    }
                                }
                            }
                        }
                    }

                    for (height = 0; height < size; ++height) {
                        BlockPos pos1 = new BlockPos(posX, posY + height, posZ);
                        BlockState block3State = world.getBlockState(pos1);

                        if (block3State.isAir() || block3State.is(BlockTags.LEAVES)) {
                            Block block = blocks.get(indexes[0]).value();
                            this.setBlock(world, pos1, block.defaultBlockState());
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }
}
