/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.datafixers.util.Either;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToSnow extends PBEffectGenerate {
    public PBEffectGenConvertToSnow() {}

    public PBEffectGenConvertToSnow(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public ResourceKey<Biome> getBiomeKey() {
        return Biomes.SNOWY_PLAINS;
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range) {
        if (!world.isClientSide()) {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0) {
                if (isBlockAnyOf(block, Either.left(Blocks.WATER))) {
                    setBlockSafe(world, pos, Blocks.ICE.defaultBlockState());
                } else if (blockState.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(world, pos)) {
                    setBlockSafe(world, pos, Blocks.SNOW.defaultBlockState());
                } else if (isBlockAnyOf(block, Either.right(BlockTags.FIRE))) {
                    setBlockSafe(world, pos, Blocks.AIR.defaultBlockState());
                } else if ((block == Blocks.LAVA && !blockState.getValue(LiquidBlock.LEVEL).equals(0)) || block == Blocks.MAGMA_BLOCK) {
                    setBlockSafe(world, pos, Blocks.COBBLESTONE.defaultBlockState());
                } else if (block == Blocks.LAVA) {
                    setBlockSafe(world, pos, Blocks.OBSIDIAN.defaultBlockState());
                }
            } else {
                Entity entity1 = lazilySpawnEntity(world, entity, random, "snow_golem", 1.0f / (20 * 20), pos);
                canSpawnEntity(world, blockState, pos, entity1);
            }
        }
    }
}
