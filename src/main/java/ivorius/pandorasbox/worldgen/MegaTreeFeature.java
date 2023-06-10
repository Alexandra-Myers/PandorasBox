package ivorius.pandorasbox.worldgen;


import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;
import java.util.Set;

public interface MegaTreeFeature {

    void setTrunk(BlockState trunk);

    void setLeaves(BlockState leaves);

    boolean place(Level world, RandomSource rand, BlockPos position);
}
