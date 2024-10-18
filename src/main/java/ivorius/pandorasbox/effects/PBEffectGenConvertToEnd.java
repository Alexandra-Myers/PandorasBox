/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.atlas.atlascore.util.ArrayListExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToEnd extends PBEffectGenerate {
    private int timesFeatureAMade = 0;
    public PBEffectGenConvertToEnd() {}

    public PBEffectGenConvertToEnd(int time, double range, int unifiedSeed)
    {
        super(time, range, 2, unifiedSeed);
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range) {
        if(world instanceof ServerLevel serverLevel) {
            BlockState blockState = world.getBlockState(pos);
            Block block = blockState.getBlock();
            ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
            blocks.addAll(Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK);
            ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
            misc.addAll(Blocks.ICE, Blocks.WATER, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.VINE, Blocks.TALL_GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM);
            blocks.addAll(PandorasBox.logs);
            misc.addAll(PandorasBox.leaves, PandorasBox.flowers);

            if (pass == 0) {
                if (isBlockAnyOf(block, Blocks.OBSIDIAN, Blocks.CHORUS_PLANT, Blocks.CHORUS_FLOWER)) {

                } else if (isBlockAnyOf(block, blocks)) {
                    setBlockSafe(world, pos, Blocks.OBSIDIAN.defaultBlockState());
                } else if (isBlockAnyOf(block, misc)) {
                    setBlockToAirSafe(world, pos);
                } else if (world.loadedAndEntityCanStandOn(pos, entity)) {
                    setBlockSafe(world, pos, Blocks.END_STONE.defaultBlockState());
                }
            } else {
                Entity enderman = lazilySpawnEntity(world, entity, random, "enderman", 1.0f / (20 * 20), pos);
                canSpawnEntity(world, blockState, pos, enderman);
            }
            if (random.nextDouble() < Math.pow(0.2, Math.floor(timesFeatureAMade / 16.0))) {
                BlockPos posBelow = pos.below();
                BlockState blockBelowState = world.getBlockState(posBelow);

                if (blockState.isAir() && !isBlockAnyOf(blockBelowState.getBlock(), Blocks.CHORUS_FLOWER, Blocks.CHORUS_PLANT, Blocks.OBSIDIAN) && blockBelowState.isRedstoneConductor(world, posBelow)) {
                    setBlockSafe(world, posBelow, Blocks.END_STONE.defaultBlockState());
                    boolean success = Feature.CHORUS_PLANT.place(FeatureConfiguration.NONE, serverLevel, serverLevel.getChunkSource().getGenerator(), random, pos);
                    if (success) timesFeatureAMade++;
                }
            }
            changeBiome(Biomes.END_BARRENS, pass, effectCenter, serverLevel);
        }
    }
}
