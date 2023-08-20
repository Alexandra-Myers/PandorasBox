/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;

import java.util.Random;

/**
 * Created by lukas on 14.02.14.
 */
public class WorldGenMegaJungleCustom extends TreeFeature implements MegaTreeFeature
{
    public BlockState leavesMetadata;
    public BlockState woodMetadata;
    public int height;
    public WorldGenMegaJungleCustom(Codec<BaseTreeFeatureConfig> configIn, int height)
    {
        super(configIn);
        this.height = height;
    }

    private void func_175932_b(World worldIn, Random p_175932_2_, BlockPos p_175932_3_, BlockState p_175932_4_)
    {
        if (p_175932_2_.nextInt(3) > 0 && worldIn.isEmptyBlock(p_175932_3_))
        {
            worldIn.setBlockAndUpdate(p_175932_3_, p_175932_4_);
        }
    }

    private void func_175930_c(World worldIn, BlockPos p_175930_2_, int p_175930_3_)
    {
        byte b0 = 2;

        for (int j = -b0; j <= 0; ++j)
        {
            this.func_175925_a(worldIn, p_175930_2_.above(j), p_175930_3_ + 1 - j);
        }
    }

    //Helper macro
    private boolean isAirLeaves(World world, BlockPos pos)
    {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        return block.isAir(blockState, world, pos) || block.is(BlockTags.LEAVES);
    }


    // Overwritten
    protected void func_175925_a(World worldIn, BlockPos p_175925_2_, int p_175925_3_)
    {
        int j = p_175925_3_ * p_175925_3_;

        for (int k = -p_175925_3_; k <= p_175925_3_ + 1; ++k)
        {
            for (int l = -p_175925_3_; l <= p_175925_3_ + 1; ++l)
            {
                int i1 = k - 1;
                int j1 = l - 1;

                if (k * k + l * l <= j || i1 * i1 + j1 * j1 <= j || k * k + j1 * j1 <= j || i1 * i1 + l * l <= j)
                {
                    BlockPos blockpos1 = p_175925_2_.offset(k, 0, l);
                    BlockState state = worldIn.getBlockState(blockpos1);

                    if (state.getBlock().isAir(state, worldIn, blockpos1) || state.getBlock().is(BlockTags.LEAVES))
                    {
                        worldIn.setBlockAndUpdate(blockpos1, this.leavesMetadata);
                    }
                }
            }
        }
    }

    protected void func_175928_b(World worldIn, BlockPos p_175928_2_, int p_175928_3_)
    {
        int j = p_175928_3_ * p_175928_3_;

        for (int k = -p_175928_3_; k <= p_175928_3_; ++k)
        {
            for (int l = -p_175928_3_; l <= p_175928_3_; ++l)
            {
                if (k * k + l * l <= j)
                {
                    BlockPos blockpos1 = p_175928_2_.offset(k, 0, l);
                    BlockState blockState = worldIn.getBlockState(blockpos1);
                    Block block = blockState.getBlock();

                    if (block.isAir(blockState, worldIn, blockpos1) || block.is(BlockTags.LEAVES))
                    {
                        worldIn.setBlockAndUpdate(blockpos1, leavesMetadata);
                    }
                }
            }
        }
    }

    @Override
    public void setTrunk(BlockState trunk) {
        woodMetadata = trunk;
    }

    @Override
    public void setLeaves(BlockState leaves) {
        leavesMetadata = leaves;
    }

    @Override
    public boolean place(IWorldGenerationReader world, Random rand, BlockPos position) {
        int i = (int) Math.round(height + (height * rand.nextDouble()) / 2);
        World worldIn = world instanceof World ? (World) world : null;
        if(worldIn == null) return false;

        this.func_175930_c(worldIn, position.above(i), 2);

        for (int j = position.getY() + i - 2 - rand.nextInt(4); j > position.getY() + i / 2; j -= 2 + rand.nextInt(4))
        {
            float f = rand.nextFloat() * (float) Math.PI * 2.0F;
            int k = position.getX() + (int) (0.5F + MathHelper.cos(f) * 4.0F);
            int l = position.getZ() + (int) (0.5F + MathHelper.sin(f) * 4.0F);
            int i1;

            for (i1 = 0; i1 < 5; ++i1)
            {
                k = position.getX() + (int) (1.5F + MathHelper.cos(f) * (float) i1);
                l = position.getZ() + (int) (1.5F + MathHelper.sin(f) * (float) i1);
                worldIn.setBlockAndUpdate(new BlockPos(k, j - 3 + i1 / 2, l), this.woodMetadata);
            }

            i1 = 1 + rand.nextInt(2);
            int j1 = j;

            for (int k1 = j - i1; k1 <= j1; ++k1)
            {
                int l1 = k1 - j1;
                this.func_175928_b(worldIn, new BlockPos(k, k1, l), 1 - l1);
            }
        }

        for (int i2 = 0; i2 < i; ++i2) {
            BlockPos blockpos1 = position.above(i2);

            if (this.isAirLeaves(worldIn, blockpos1)) {
                worldIn.setBlockAndUpdate(blockpos1, this.woodMetadata);

                if (i2 > 0) {
                    this.func_175932_b(worldIn, rand, blockpos1.west(), Blocks.VINE.defaultBlockState().setValue(VineBlock.EAST, true));
                    this.func_175932_b(worldIn, rand, blockpos1.north(), Blocks.VINE.defaultBlockState().setValue(VineBlock.SOUTH, true));
                }
            }

            if (i2 < i - 1) {
                BlockPos blockpos2 = blockpos1.east();

                if (this.isAirLeaves(worldIn, blockpos2)) {
                    worldIn.setBlockAndUpdate(blockpos2, this.woodMetadata);

                    if (i2 > 0) {
                        this.func_175932_b(worldIn, rand, blockpos2.east(), Blocks.VINE.defaultBlockState().setValue(VineBlock.WEST, true));
                        this.func_175932_b(worldIn, rand, blockpos2.north(), Blocks.VINE.defaultBlockState().setValue(VineBlock.SOUTH, true));
                    }
                }

                BlockPos blockpos3 = blockpos1.south().east();

                if (this.isAirLeaves(worldIn, blockpos3)) {
                    worldIn.setBlockAndUpdate(blockpos3, this.woodMetadata);

                    if (i2 > 0) {
                        this.func_175932_b(worldIn, rand, blockpos3.east(), Blocks.VINE.defaultBlockState().setValue(VineBlock.WEST, true));
                        this.func_175932_b(worldIn, rand, blockpos3.south(), Blocks.VINE.defaultBlockState().setValue(VineBlock.NORTH, true));
                    }
                }

                BlockPos blockpos4 = blockpos1.south();

                if (this.isAirLeaves(worldIn, blockpos4)) {
                    worldIn.setBlockAndUpdate(blockpos4, this.woodMetadata);

                    if (i2 > 0) {
                        this.func_175932_b(worldIn, rand, blockpos4.west(), Blocks.VINE.defaultBlockState().setValue(VineBlock.EAST, true));
                        this.func_175932_b(worldIn, rand, blockpos4.south(), Blocks.VINE.defaultBlockState().setValue(VineBlock.NORTH, true));
                    }
                }
            }
        }
        return true;
    }
}
