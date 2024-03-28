/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

import static net.minecraft.block.Block.dropResources;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenConvertToLifeless extends PBEffectGenerate
{
    public PBEffectGenConvertToLifeless() {}

    public PBEffectGenConvertToLifeless(int time, double range, int unifiedSeed)
    {
        super(time, range, 1, unifiedSeed);
    }

    @Override
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (pass == 0) {
            ArrayListExtensions<Block> blocks = new ArrayListExtensions<>();
            ArrayListExtensions<Block> iCTWTGASTB = new ArrayListExtensions<>();
            ArrayListExtensions<Block> weird = new ArrayListExtensions<>();
            ArrayListExtensions<Block> hmm = new ArrayListExtensions<>();
            hmm.addAll(Blocks.NETHERRACK, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.END_STONE, Blocks.BASALT, Blocks.BLACKSTONE);
            blocks.addAll(Blocks.GRASS, Blocks.FERN, Blocks.LARGE_FERN, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS);
            weird.addAll(Blocks.SAND, Blocks.RED_SAND, Blocks.MYCELIUM, Blocks.GRASS_BLOCK, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.CAKE);
            iCTWTGASTB.addAll(Blocks.VINE, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM_BLOCK, Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM);
            iCTWTGASTB.addAll(PandorasBox.logs, PandorasBox.leaves);
            weird.addAll(PandorasBox.wool);
            blocks.addAll(PandorasBox.flowers);
            hmm.addAll(PandorasBox.stained_terracotta);
            FluidState fluidstate = world.getFluidState(pos);
            Material material = state.getMaterial();
            if (fluidstate.is(FluidTags.WATER)) {
                if (block instanceof IBucketPickupHandler && ((IBucketPickupHandler) block).takeLiquid(world, pos, state) != Fluids.EMPTY) return;

                if (material == Material.WATER_PLANT || material == Material.REPLACEABLE_WATER_PLANT) {
                    TileEntity blockentity = state.hasTileEntity() ? world.getBlockEntity(pos) : null;
                    dropResources(state, world, pos, blockentity);
                }
                setBlockToAirSafe(world, pos);
            } else if (isBlockAnyOf(block, Blocks.ICE, Blocks.WATER, Blocks.LAVA, Blocks.SNOW, Blocks.SNOW_BLOCK)) {
                setBlockToAirSafe(world, pos);
            } else if (isBlockAnyOf(block, iCTWTGASTB)) {
                setBlockToAirSafe(world, pos);
            } else if (isBlockAnyOf(block, blocks)) {
                setBlockSafe(world, pos, Blocks.DEAD_BUSH.defaultBlockState());
            } else if (isBlockAnyOf(block, weird)) {
                setBlockSafe(world, pos, Blocks.DIRT.defaultBlockState());
            } else if (isBlockAnyOf(block, hmm)) {
                setBlockSafe(world, pos, Blocks.STONE.defaultBlockState());
            } else if (isBlockAnyOf(block, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.RED_SAND)) {
                setBlockSafe(world, pos, Blocks.SAND.defaultBlockState());
            }
        }
    }
}
