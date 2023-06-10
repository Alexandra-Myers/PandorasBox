package ivorius.pandorasbox.worldgen;


import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public interface AccessibleTreeFeature {

    void setMetas(int[] newMetas);

    void setSoil(Block newSoil);

    boolean place(Level worldIn, RandomSource rand, BlockPos position);
}
