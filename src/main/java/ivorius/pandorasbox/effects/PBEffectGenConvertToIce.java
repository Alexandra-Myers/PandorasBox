/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToIce extends PBEffectGenerate {
    public PBEffectGenConvertToIce() {}

    public PBEffectGenConvertToIce(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range) {
        if(world instanceof ServerLevel serverLevel) {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();

            if (pass == 0) {
                if (isBlockAnyOf(block, Blocks.WATER)) {
                    setBlockSafe(world, pos, Blocks.ICE.defaultBlockState());
                } else if (blockState.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(world, pos)) {
                    setBlockSafe(world, pos, Blocks.SNOW.defaultBlockState());
                } else if (isBlockAnyOf(block, Blocks.FIRE, Blocks.SOUL_FIRE)) {
                    setBlockSafe(world, pos, Blocks.AIR.defaultBlockState());
                } else if ((block == Blocks.LAVA && !blockState.getValue(LiquidBlock.LEVEL).equals(0)) || block == Blocks.MAGMA_BLOCK) {
                    setBlockSafe(world, pos, Blocks.ICE.defaultBlockState());
                } else if (block == Blocks.LAVA) {
                    setBlockSafe(world, pos, Blocks.PACKED_ICE.defaultBlockState());
                } else if (world.loadedAndEntityCanStandOn(pos, entity)) {
                    int mode = random.nextInt(6);

                    if (mode == 0)
                        setBlockSafe(world, pos, Blocks.ICE.defaultBlockState());
                    else if (mode == 1)
                        setBlockSafe(world, pos, Blocks.PACKED_ICE.defaultBlockState());
                    else if (mode == 2)
                        setBlockSafe(world, pos, Blocks.BLUE_ICE.defaultBlockState());
                }
            } else {
                Entity snowGolem = lazilySpawnEntity(world, entity, random, "snow_golem", 1.0f / (20 * 20), pos);
                canSpawnEntity(world, blockState, pos, snowGolem);
            }
            changeBiome(Biomes.SNOWY_TAIGA, pass, effectCenter, serverLevel);
        }
    }
}
