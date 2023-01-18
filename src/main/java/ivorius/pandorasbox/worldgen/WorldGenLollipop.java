/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.worldgen;

import com.mojang.serialization.Codec;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public class WorldGenLollipop extends TreeFeature implements AccessibleTreeFeature
{
    public static int[] metas;
    public static Block soil;
    public final int addition;

    public WorldGenLollipop(Codec<BaseTreeFeatureConfig> configIn, int addition)
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
    public boolean place(IWorldGenerationReader worldIn, Random rand, BlockPos position) {
        int l = rand.nextInt(addition) + 5;
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        for(Block block1 : ForgeRegistries.BLOCKS) {
            if (BlockTags.WOOL.contains(block1)) {
                blocks.add(block1);
            }
        }
        World world = worldIn instanceof World ? (World) worldIn : null;

        boolean flag = true;

        int par3 = position.getX();
        int par4 = position.getY();
        int par5 = position.getZ();

        if(world == null) return false;
        if (par4 >= 1 && par4 + l + 1 <= 256)
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
                        if (i1 >= 0 && i1 < 256)
                        {
                            BlockPos pos = new BlockPos(j1, i1, k1);
                            Block block = world.getBlockState(pos).getBlock();
                        }
                        else
                        {
                            flag = false;
                        }
                    }
                }
            }

            if (!flag)
            {
                return false;
            }
            else
            {
                BlockPos pos = new BlockPos(par3, par4 - 1, par5);
                BlockState block2State = world.getBlockState(pos);
                Block block2 = block2State.getBlock();
                boolean rotated = rand.nextBoolean();

                boolean isSoil = block2 == soil;
                if (isSoil && par4 < 256 - l - 1)
                {
                    block2.onPlantGrow(block2State, world, pos, new BlockPos(par3, par4, par5));
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
                                    Block block1 = block1State.getBlock();

                                    if (block1.isAir(block1State, world, pos1) || block1.is(BlockTags.LEAVES))
                                    {
                                        this.setBlock(worldIn, pos1, blocks.get(metas[rand.nextInt(metas.length)]).defaultBlockState());
                                    }
                                }
                            }
                        }
                    }

                    for (k2 = 0; k2 < l; ++k2)
                    {
                        BlockPos pos1 = new BlockPos(par3, par4 + k2, par5);
                        BlockState block3State = world.getBlockState(pos1);
                        Block block3 = block3State.getBlock();

                        if (block3State.isAir(world, pos1) || block3.is(BlockTags.LEAVES))
                        {
                            this.setBlock(worldIn, pos1, blocks.get(metas[0]).defaultBlockState());
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