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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

import java.util.Random;

public class WorldGenLollipop extends TreeFeature implements AccessibleTreeFeature
{
    public static int[] metas;
    public static Block soil;
    public final int addition;

    public WorldGenLollipop(Codec<TreeConfiguration> configIn, int addition)
    {
        super(configIn);
        this.addition = addition;
    }

    @Override
    public void setMetas(int[] newMetas) {
        metas = newMetas;
    }

    @Override
    public void setSoil(Block newSoil) {
        soil = newSoil;
    }

    @Override
    public boolean place(Level world, Random rand, BlockPos position) {
        int l = rand.nextInt(addition) + 5;
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        blocks.addAll(PandorasBox.wool);

        boolean flag = true;

        int par3 = position.getX();
        int par4 = position.getY();
        int par5 = position.getZ();

        if(world == null) return false;
        if (par4 >= 1 && par4 + l + 1 <= 320)
        {
            int j1;
            int k1;

            for (int i1 = par4; i1 <= par4 + 1 + l; ++i1)
            {
                byte b0 = 1;

                if (i1 == par4)
                {
                    b0 = 0;
                }

                if (i1 >= par4 + 1 + l - 2)
                {
                    b0 = 2;
                }

                for (j1 = par3 - b0; j1 <= par3 + b0 && flag; ++j1)
                {
                    for (k1 = par5 - b0; k1 <= par5 + b0 && flag; ++k1)
                    {
                        flag = !(i1 >= 0 && i1 < 320);
                    }
                }
            }

            if (!flag)
            {
                return false;
            }
            else
            {
                BlockPos pos = position.below();
                BlockState block2State = world.getBlockState(pos);
                Block block2 = block2State.getBlock();
                boolean rotated = rand.nextBoolean();

                boolean isSoil = block2 == soil;
                if (isSoil && par4 < 320 - l - 1)
                {

                    int k2;

                    for (int shift = -1; shift <= 1; shift++)
                    {
                        for (int s = -l / 2; s <= l / 2; s++)
                        {
                            for (int y = -l / 2; y <= l / 2; y++)
                            {
                                if (s * s + y * y <= (l * l / 4 - shift * shift * 4))
                                {
                                    int x = (!rotated ? s : shift) + par3;
                                    int z = (rotated ? s : shift) + par5;
                                    int rY = y + par4 + l;

                                    BlockPos pos1 = new BlockPos(x, rY, z);
                                    BlockState block1State = world.getBlockState(pos1);

                                    if (block1State.isAir() || block1State.is(BlockTags.LEAVES))
                                    {
                                        this.setBlock(world, pos1, blocks.get(metas[rand.nextInt(metas.length)]).defaultBlockState());
                                    }
                                }
                            }
                        }
                    }

                    for (k2 = 0; k2 < l; ++k2)
                    {
                        BlockPos pos1 = new BlockPos(par3, par4 + k2, par5);
                        BlockState block3State = world.getBlockState(pos1);

                        if (block3State.isAir() || block3State.is(BlockTags.LEAVES))
                        {
                            this.setBlock(world, pos1, blocks.get(metas[0]).defaultBlockState());
                        }
                    }

                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        else
        {
            return false;
        }
    }
}