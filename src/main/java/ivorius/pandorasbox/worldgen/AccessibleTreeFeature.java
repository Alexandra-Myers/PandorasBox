package ivorius.pandorasbox.worldgen;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;

import java.util.Random;
import java.util.Set;

public interface AccessibleTreeFeature {

    void setMetas(int[] newMetas);

    void setSoil(Block newSoil);

    boolean place(IWorldGenerationReader worldIn, Random rand, BlockPos position);
}
