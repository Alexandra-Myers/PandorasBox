/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.datafixers.util.Either;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.init.FeatureInit;
import ivorius.pandorasbox.worldgen.AccessibleTreeFeature;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToHFT extends PBEffectGenerate {
    public int[] groundMetas;
    public PBEffectGenConvertToHFT() {}

    public PBEffectGenConvertToHFT(int time, double range, int unifiedSeed, int[] groundMetas) {
        super(time, range, 2, unifiedSeed);

        this.groundMetas = groundMetas;
    }

    @Override
    public ResourceKey<Biome> getBiomeKey() {
        return Biomes.CHERRY_GROVE;
    }

    @Override
    public void generateOnBlock(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range) {
        if (!level.isClientSide()) {
            BlockState blockState = level.getBlockState(pos);
            Block block = blockState.getBlock();
            HolderSet.Named<Block> terracotta = BuiltInRegistries.BLOCK.getOrThrow(PandorasBox.ALL_TERRACOTTA);
            Block placeBlock = terracotta.get(groundMetas[random.nextInt(groundMetas.length)] % terracotta.size()).value();
            if (pass == 0) {
                if (isBlockAnyOf(block, Either.left(Blocks.LAVA), Either.right(BlockTags.LOGS), Either.right(BlockTags.LEAVES))) {
                    setBlockToAirSafe(level, pos);
                } else if (!isBlockAnyOf(block, Either.right(BlockTags.WOOL), Either.right(PandorasBox.ALL_TERRACOTTA)) && Block.isShapeFullBlock(blockState.getBlockSupportShape(level, pos))) {
                    setBlockSafe(level, pos, placeBlock.defaultBlockState());
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
                    treeFeature.place(level, random, pos);
                } else if (random.nextInt(5 * 5) == 0) {
                    if (level.getBlockState(pos.below()).getBlock() == placeBlock && level.getBlockState(pos).isAir()) {
                        if (random.nextBoolean()) {
                            setBlockSafe(level, pos, Blocks.CAKE.defaultBlockState());
                        } else {
                            ItemEntity entityItem = new ItemEntity(level, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, new ItemStack(Items.COOKIE));
                            entityItem.setPickUpDelay(20);
                            level.addFreshEntity(entityItem);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.writeToNBT(compound, registryAccess);

        compound.putIntArray("groundMetas", groundMetas);
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.readFromNBT(compound, registryAccess);

        groundMetas = compound.getIntArray("groundMetas");
    }
}
