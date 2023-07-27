package ivorius.pandorasbox.worldgen;


import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface MegaTreeFeature {

    void setTrunk(BlockState trunk);

    void setLeaves(BlockState leaves);

    boolean place(Level world, RandomSource rand, BlockPos position);
}
