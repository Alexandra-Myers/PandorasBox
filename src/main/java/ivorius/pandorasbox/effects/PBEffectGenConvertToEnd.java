/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.datafixers.util.Either;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.phys.Vec3;

import static ivorius.pandorasbox.effects.PBEffectGenConvertToNether.NetherBiome.expFromRatio;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToEnd extends PBEffectGenerate {
    public PBEffectGenConvertToEnd() {}

    public PBEffectGenConvertToEnd(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public ResourceKey<Biome> getBiomeKey() {
        return Biomes.END_BARRENS;
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range) {
        if(world instanceof ServerLevel serverLevel) {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0 && isBlockAnyOf(block, Either.right(ConventionalBlockTags.OBSIDIANS), Either.left(Blocks.CHORUS_PLANT), Either.left(Blocks.CHORUS_FLOWER))) {
                if (isBlockAnyOf(block, Either.right(BlockTags.LEAVES), Either.right(BlockTags.FLOWERS), Either.right(BlockTags.SNOW), Either.left(Blocks.ICE), Either.left(Blocks.WATER), Either.left(Blocks.VINE), Either.left(Blocks.SHORT_GRASS), Either.left(Blocks.TALL_GRASS), Either.left(Blocks.FERN), Either.left(Blocks.LARGE_FERN), Either.left(Blocks.SEAGRASS), Either.left(Blocks.TALL_SEAGRASS), Either.left(Blocks.BROWN_MUSHROOM_BLOCK), Either.left(Blocks.RED_MUSHROOM_BLOCK), Either.right(BlockTags.LOGS))) {
                    setBlockSafe(world, pos, Blocks.OBSIDIAN.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.left(Blocks.BROWN_MUSHROOM), Either.left(Blocks.RED_MUSHROOM))) {
                    setBlockToAirSafe(world, pos);
                } else if (world.loadedAndEntityCanStandOn(pos, entity)) {
                    setBlockSafe(world, pos, Blocks.END_STONE.defaultBlockState());
                }
            } else if (pass != 0) {
                Entity enderman = lazilySpawnEntity(world, entity, random, "enderman", 1.0f / (20 * 20), pos);
                canSpawnEntity(world, blockState, pos, enderman);
            }
            if (random.nextDouble() < Math.pow(0.02, expFromRatio(getRatioDone(entity.getTicksForEffect(this) + 1)))) {
                BlockPos posBelow = pos.below();
                BlockState blockBelowState = world.getBlockState(posBelow);

                if (blockState.isAir() && !isBlockAnyOf(blockBelowState.getBlock(), Either.right(ConventionalBlockTags.OBSIDIANS), Either.left(Blocks.CHORUS_FLOWER), Either.left(Blocks.CHORUS_PLANT)) && blockBelowState.isRedstoneConductor(world, posBelow)) {
                    setBlockSafe(world, posBelow, Blocks.END_STONE.defaultBlockState());
                    Feature.CHORUS_PLANT.place(FeatureConfiguration.NONE, serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos);
                }
            }
        }
    }
}
