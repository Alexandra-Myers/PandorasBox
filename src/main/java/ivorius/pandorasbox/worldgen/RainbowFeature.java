package ivorius.pandorasbox.worldgen;

import com.mojang.serialization.Codec;
import ivorius.pandorasbox.worldgen.configuration.LollipopConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.NotNull;

public class RainbowFeature extends Feature<@NotNull LollipopConfiguration> {
    public RainbowFeature(Codec<LollipopConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<@NotNull LollipopConfiguration> featurePlaceContext) {
        RandomSource rand = featurePlaceContext.random();
        WorldGenLevel world = featurePlaceContext.level();
        HolderSet<Block> blocks = featurePlaceContext.config().blocks();
        if (blocks.size() == 0) return false;
        int offset = rand.nextInt(blocks.size());

        BlockPos position = featurePlaceContext.origin();

        int posX = position.getX();
        int posY = position.getY();
        int posZ = position.getZ();

        if (world.getBlockState(position.above()).isAir()) {
            boolean rotated = rand.nextBoolean();
            int size = featurePlaceContext.config().size().getValue(rand);

            for (int shift = -1; shift <= 1; shift++) {
                for (int s = -size / 2; s <= size / 2; s++) {
                    for (int y = -size / 2; y <= size / 2; y++) {
                        int distance = Mth.floor(Mth.sqrt(s * s + y * y));

                        if (distance <= (size / 2 - Mth.floor(Mth.sqrt(shift * shift)) * 2) && distance > size / 4) {
                            int x = (!rotated ? s : shift) + posX;
                            int z = (rotated ? s : shift) + posZ;
                            int rY = y + posY;

                            BlockPos placePos = new BlockPos(x, rY, z);
                            BlockState block1State = world.getBlockState(placePos);

                            if (block1State.isAir() || block1State.is(BlockTags.LEAVES)) {
                                int meta = distance;
                                if (meta < 0) meta = 0;
                                meta += offset;

                                this.setBlock(world, placePos, blocks.get(meta % blocks.size()).value().defaultBlockState());
                            }
                        }
                    }
                }
            }

            return true;
        }

        return false;
    }
}
