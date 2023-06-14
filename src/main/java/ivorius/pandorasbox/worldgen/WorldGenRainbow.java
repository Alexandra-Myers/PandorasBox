/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.worldgen;

import com.mojang.serialization.Codec;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

/**
 * Created by lukas on 14.02.14.
 */
public class WorldGenRainbow extends TreeFeature implements AccessibleTreeFeature
{
    public final int addition;
    public Block soil;

    public WorldGenRainbow(Codec<TreeConfiguration> configIn, int addition)
    {
        super(configIn);

        this.addition = addition;
    }

    @Override
    public void setMetas(int[] newMetas) {

    }

    @Override
    public void setSoil(Block newSoil) {
        soil = newSoil;
    }

    @Override
    public boolean place(Level world, RandomSource rand, BlockPos position) {
        int par3 = position.getX();
        int par4 = position.getY();
        int par5 = position.getZ();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        blocks.addAll(PandorasBox.wool);

        if(world == null) return false;
        if (world.getBlockState(position).getBlock() == soil && world.getBlockState(position.above()).isAir())
        {
            boolean rotated = rand.nextBoolean();
            int l = rand.nextInt(addition) + 5;

            for (int shift = -1; shift <= 1; shift++)
            {
                for (int s = -l / 2; s <= l / 2; s++)
                {
                    for (int y = -l / 2; y <= l / 2; y++)
                    {
                        int distance = Mth.floor(Mth.sqrt(s * s + y * y));

                        if (distance <= (l / 2 - Mth.floor(Mth.sqrt(shift * shift)) * 2) && distance > l / 4)
                        {
                            int x = (!rotated ? s : shift) + par3;
                            int z = (rotated ? s : shift) + par5;
                            int rY = y + par4;

                            BlockPos placePos = new BlockPos(x, rY, z);
                            BlockState block1State = world.getBlockState(placePos);

                            if (block1State.isAir() || block1State.is(BlockTags.LEAVES))
                            {
                                int meta = distance;
                                if (meta < 0)
                                {
                                    meta = 0;
                                }
                                if (meta > 15)
                                {
                                    meta = 15;
                                }

                                this.setBlock(world, placePos, blocks.get(meta).defaultBlockState());
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
