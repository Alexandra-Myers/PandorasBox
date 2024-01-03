/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.FeatureInit;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.worldgen.AccessibleTreeFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

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
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range)
    {
        if(world instanceof ServerLevel serverLevel) {
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
            if (pass == 0) {
                if (isBlockAnyOf(block, iCTWTGASTB)) {
                    setBlockToAirSafe(world, pos);
                } else if (!isBlockAnyOf(block, blocks) && Block.isShapeFullBlock(blockState.getBlockSupportShape(world, pos))) {
                    setBlockSafe(world, pos, placeBlock.defaultBlockState());
                }
            } else if (pass == 1) {
                if (random.nextInt(10 * 10) == 0) {
                    int[] lolliColors = new int[random.nextInt(4) + 1];
                    for (int i = 0; i < lolliColors.length; i++) {
                        lolliColors[i] = random.nextInt(16);
                    }

                    AccessibleTreeFeature treeFeature = (AccessibleTreeFeature) FeatureInit.RAINBOW;

                    if (random.nextFloat() > 0.5) {
                        treeFeature = (AccessibleTreeFeature) FeatureInit.LOLIPOP;
                    } else if (random.nextFloat() > 0.4) {
                        treeFeature = (AccessibleTreeFeature) FeatureInit.COLOURFUL_TREE;
                    }
                    treeFeature.setMetas(lolliColors);
                    treeFeature.setSoil(placeBlock);
                    treeFeature.place(world, random, pos);
                } else if (random.nextInt(5 * 5) == 0) {
                    if (world.getBlockState(pos.below()).getBlock() == placeBlock && world.getBlockState(pos).isAir()) {
                        if (random.nextBoolean()) {
                            setBlockSafe(world, pos, Blocks.CAKE.defaultBlockState());
                        } else {
                            ItemEntity entityItem = new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, new ItemStack(Items.COOKIE));
                            entityItem.setPickUpDelay(20);
                            world.addFreshEntity(entityItem);
                        }
                    }
                }
            }
            changeBiome(Biomes.CHERRY_GROVE, pass, effectCenter, serverLevel);
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);

        compound.putIntArray("groundMetas", groundMetas);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        groundMetas = compound.getIntArray("groundMetas");
    }
}
