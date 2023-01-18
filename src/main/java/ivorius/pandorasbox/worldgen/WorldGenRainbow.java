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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.World;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;
import java.util.Set;

/**
 * Created by lukas on 14.02.14.
 */
public class WorldGenRainbow extends TreeFeature implements AccessibleTreeFeature
{
    public final int addition;
    public Block soil;

    public WorldGenRainbow(Codec<BaseTreeFeatureConfig> configIn, int addition)
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
    public boolean place(IWorldGenerationReader worldIn, Random rand, BlockPos position) {
        World world = worldIn instanceof World ? (World) worldIn : null;
        int par3 = position.getX();
        int par4 = position.getY();
        int par5 = position.getZ();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        for(Block block1 : ForgeRegistries.BLOCKS) {
            if (BlockTags.WOOL.contains(block1)) {
                blocks.add(block1);
            }
        }

        if(world == null) return false;
        if (world.getBlockState(position).getBlock() == soil && world.getBlockState(position.above()).isAir(world, position))
        {
            boolean rotated = rand.nextBoolean();
            int l = rand.nextInt(addition) + 5;

            for (int shift = -1; shift <= 1; shift++)
            {
                for (int s = -l / 2; s <= l / 2; s++)
                {
                    for (int y = -l / 2; y <= l / 2; y++)
                    {
                        int distance = MathHelper.floor(MathHelper.sqrt(s * s + y * y));

                        if (distance <= (l / 2 - MathHelper.floor(MathHelper.sqrt(shift * shift)) * 2) && distance > l / 4)
                        {
                            int x = (!rotated ? s : shift) + par3;
                            int z = (rotated ? s : shift) + par5;
                            int rY = y + par4;

                            BlockPos placePos = new BlockPos(x, rY, z);
                            BlockState block1State = world.getBlockState(placePos);
                            Block block1 = block1State.getBlock();

                            if (block1.isAir(block1State, world, placePos) || block1.is(BlockTags.LEAVES))
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
