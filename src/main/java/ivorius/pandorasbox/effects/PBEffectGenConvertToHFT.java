/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.worldgen.AccessibleTreeFeature;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToHFT extends PBEffectGenerate
{
    public int[] groundMetas;
    public PBEffectGenConvertToHFT() {}

    public PBEffectGenConvertToHFT(int time, double range, int unifiedSeed, int[] groundMetas)
    {
        super(time, range, 2, unifiedSeed);

        this.groundMetas = groundMetas;
    }

    @Override
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range)
    {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
        ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> iCTWTGASTB = new ArrayListExtensions<>();
        iCTWTGASTB.add(Blocks.LAVA);
        iCTWTGASTB.addAll(PandorasBox.logs, PandorasBox.leaves);
        blocks.addAll(PandorasBox.terracotta, PandorasBox.wool);
        misc.addAll(PandorasBox.terracotta);
        Block placeBlock = misc.get(groundMetas[random.nextInt(groundMetas.length)]);
        if (pass == 0)
        {
            if (isBlockAnyOf(block, iCTWTGASTB))
            {
                setBlockToAirSafe(world, pos);
            }
            else if (!isBlockAnyOf(block, blocks) && Block.isShapeFullBlock(blockState.getBlockSupportShape(world, pos)))
            {
                if (world instanceof ServerWorld)
                {
                    setBlockSafe(world, pos, placeBlock.defaultBlockState());
                }
            }
        }
        else if (pass == 1)
        {
            if (world instanceof ServerWorld)
            {
                if (random.nextInt(10 * 10) == 0)
                {
                    int[] lolliColors = new int[random.nextInt(4) + 1];
                    for (int i = 0; i < lolliColors.length; i++)
                    {
                        lolliColors[i] = random.nextInt(16);
                    }

                    AccessibleTreeFeature treeFeature = (AccessibleTreeFeature) PandorasBox.instance.RAINBOW;

                    if (random.nextFloat() > 0.5) {
                        treeFeature = (AccessibleTreeFeature) PandorasBox.instance.LOLIPOP;
                    } else if (random.nextFloat() > 0.4) {
                        treeFeature = (AccessibleTreeFeature) PandorasBox.instance.COLOURFUL_TREE;
                    }
                    treeFeature.setMetas(lolliColors);
                    treeFeature.setSoil(placeBlock);
                    treeFeature.place(world, random, pos);
                }
                else if (random.nextInt(5 * 5) == 0)
                {
                    if (world.getBlockState(pos.below()).getBlock() == placeBlock && world.getBlockState(pos).isAir(world, pos))
                    {
                        if (random.nextBoolean())
                        {
                            setBlockSafe(world, pos, Blocks.CAKE.defaultBlockState());
                        }
                        else
                        {
                            ItemEntity entityItem = new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, new ItemStack(Items.COOKIE));
                            entityItem.setPickUpDelay(20);
                            world.addFreshEntity(entityItem);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);

        compound.putIntArray("groundMetas", groundMetas);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);

        groundMetas = compound.getIntArray("groundMetas");
    }
}
