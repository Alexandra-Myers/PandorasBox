package ivorius.pandorasbox.worldgen;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;

import java.util.Random;
import java.util.Set;

public interface MegaTreeFeature {

    void setTrunk(BlockState trunk);

    void setLeaves(BlockState leaves);

    boolean place(IWorldGenerationReader world, Random rand, BlockPos position);
}
