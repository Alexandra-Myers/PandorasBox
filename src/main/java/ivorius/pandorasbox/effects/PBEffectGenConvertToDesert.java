/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToDesert extends PBEffectGenerate
{
    public PBEffectGenConvertToDesert() {}
    public PBEffectGenConvertToDesert(int time, double range, int unifiedSeed)
    {
        super(time, range, 1, unifiedSeed);
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, int pass, BlockPos pos, double range)
    {
        if(world instanceof ServerLevel serverLevel) {
            Block block = world.getBlockState(pos).getBlock();

            if (pass == 0) {
                ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
                blocks.addAll(Blocks.ICE, Blocks.WATER, Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.VINE, Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.BROWN_MUSHROOM, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM, Blocks.RED_MUSHROOM_BLOCK);
                ArrayListExtensions<Block> soil = new ArrayListExtensions<>();
                ArrayListExtensions<Block> misc = new ArrayListExtensions<>();
                soil.addAll(Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.GRASS_BLOCK, Blocks.NETHERRACK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.DIRT, Blocks.MYCELIUM, Blocks.MOSS_BLOCK, Blocks.RED_SAND);
                misc.addAll(Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.BLACKSTONE, Blocks.BASALT, Blocks.END_STONE, Blocks.DEEPSLATE, Blocks.TUFF, Blocks.RED_SANDSTONE);
                blocks.addAll(PandorasBox.flowers, PandorasBox.leaves, PandorasBox.logs);
                misc.addAll(PandorasBox.terracotta);
                if (isBlockAnyOf(block, blocks)) {
                    setBlockToAirSafe(world, pos);
                } else if (isBlockAnyOf(block, soil)) {
                    setBlockSafe(world, pos, Blocks.SAND.defaultBlockState());

                    if (world.getBlockState(pos.above()).isAir()) {
                        if (random.nextInt(20 * 20) == 0) {
                            setBlockSafe(world, pos.above(1), Blocks.CACTUS.defaultBlockState());
                            setBlockSafe(world, pos.above(2), Blocks.CACTUS.defaultBlockState());
                            setBlockSafe(world, pos.above(3), Blocks.CACTUS.defaultBlockState());
                        }
                    }
                } else if (isBlockAnyOf(block, misc)){
                    setBlockSafe(world, pos, Blocks.SANDSTONE.defaultBlockState());
                }
            }
            changeBiome(Biomes.DESERT, pass, effectCenter, serverLevel);
        }
    }
}
