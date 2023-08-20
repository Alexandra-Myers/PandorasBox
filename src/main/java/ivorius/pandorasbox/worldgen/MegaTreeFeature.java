package ivorius.pandorasbox.worldgen;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.IWorldGenerationReader;

import java.util.Random;

public interface MegaTreeFeature {

    void setTrunk(BlockState trunk);

    void setLeaves(BlockState leaves);

    boolean place(IWorldGenerationReader world, Random rand, BlockPos position);
}
