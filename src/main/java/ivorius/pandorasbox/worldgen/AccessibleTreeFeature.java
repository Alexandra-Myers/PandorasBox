package ivorius.pandorasbox.worldgen;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Random;

public interface AccessibleTreeFeature {

    void setMetas(int[] newMetas);

    void setSoil(Block newSoil);

    boolean place(Level worldIn, Random rand, BlockPos position);
}
