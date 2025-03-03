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
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
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
public class PBEffectGenConvertToHomo extends PBEffectGenerate {
    public PBEffectGenConvertToHomo() {}

    public PBEffectGenConvertToHomo(int time, double range, int unifiedSeed)
    {
        super(time, range, 3, unifiedSeed);
    }

    @Override
    public ResourceKey<Biome> getBiomeKey() {
        return Biomes.FLOWER_FOREST;
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range) {
        if (!world.isClientSide()) {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0) {
                if (isBlockAnyOf(block, Either.right(BlockTags.SNOW))) {
                    setBlockToAirSafe(world, pos);
                } else if (isBlockAnyOf(block, Either.right(PandorasBox.ALL_TERRACOTTA), Either.right(ConventionalBlockTags.STONES), Either.right(ConventionalBlockTags.COBBLESTONES), Either.right(BlockTags.BASE_STONE_NETHER), Either.right(BlockTags.WITHER_SUMMON_BASE_BLOCKS), Either.right(BlockTags.NYLIUM), Either.right(BlockTags.DIRT), Either.right(BlockTags.SAND), Either.right(ConventionalBlockTags.SANDSTONE_BLOCKS), Either.left(Blocks.END_STONE)) && !isBlockAnyOf(block, Either.left(Blocks.DIRT), Either.left(Blocks.GRASS_BLOCK))) {
                    if (world.getBlockState(pos.above()).isAir()) {
                        setBlockSafe(world, pos, Blocks.GRASS_BLOCK.defaultBlockState());
                    } else {
                        setBlockSafe(world, pos, Blocks.DIRT.defaultBlockState());
                    }
                } else if (isBlockAnyOf(block, Either.right(BlockTags.FIRE), Either.left(Blocks.BROWN_MUSHROOM), Either.left(Blocks.RED_MUSHROOM), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM_BLOCK))) {
                    setBlockToAirSafe(world, pos);
                }

                if (isBlockAnyOf(block, Either.right(ConventionalBlockTags.OBSIDIANS), Either.left(Blocks.ICE), Either.left(Blocks.LAVA))) {
                    setBlockSafe(world, pos, Blocks.WATER.defaultBlockState());
                }
            } else if (pass == 1) {
                if (random.nextInt(15 * 15) == 0) {
                    int[] lolliColors = new int[random.nextInt(4) + 1];
                    for (int i = 0; i < lolliColors.length; i++) {
                        lolliColors[i] = random.nextInt(16);
                    }

                    AccessibleTreeFeature treeFeature = (AccessibleTreeFeature) FeatureInit.RAINBOW;
                    treeFeature.setMetas(lolliColors);
                    treeFeature.setSoil(Blocks.GRASS_BLOCK);
                    treeFeature.place(world, random, pos);
                } else if (blockState.isAir() && Blocks.POPPY.defaultBlockState().canSurvive(world, pos)) {
                    if (random.nextInt(3 * 3) == 0) {
                        HolderSet.Named<Block> flowers = BuiltInRegistries.BLOCK.getOrThrow(BlockTags.FLOWERS);
                        int flowerIndex = random.nextInt(flowers.size());

                        setBlockSafe(world, pos, flowers.get(flowerIndex).value().defaultBlockState());
                    }
                }
            } else {
                Sheep sheep = (Sheep) lazilySpawnEntity(world, entity, random, "sheep", 1.0f / (10 * 10), pos);
                if (canSpawnEntity(world, blockState, pos, sheep)) {
                    sheep.setColor(DyeColor.byId(random.nextInt(16)));
                }
            }
        }
    }
}
