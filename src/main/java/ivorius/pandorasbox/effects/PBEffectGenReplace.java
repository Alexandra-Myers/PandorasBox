/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

import static net.minecraft.block.Block.dropResources;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenReplace extends PBEffectGenerate {
    public Block[] blocks;
    public Block[] blocksToReplace;
    public PBEffectGenReplace() {}

    public PBEffectGenReplace(int time, double range, int unifiedSeed, Block[] blocks, Block[] blocksToReplace) {
        super(time, range, 1, unifiedSeed);

        this.blocks = blocks;
        this.blocksToReplace = blocksToReplace;
    }

    @Override
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range) {
        if (!world.isClientSide) {
            Block newBlock = blocks[random.nextInt(blocks.length)];
            BlockState prevState = world.getBlockState(pos);
            Block prevBlock = prevState.getBlock();
            boolean replace = false;
            for (Block block : blocksToReplace) {
                if (block == Blocks.WATER) {
                    FluidState fluidstate = world.getFluidState(pos);
                    Material material = prevState.getMaterial();
                    if (fluidstate.is(FluidTags.WATER)) {
                        if (prevBlock instanceof IBucketPickupHandler && ((IBucketPickupHandler) prevBlock).takeLiquid(world, pos, prevState) != Fluids.EMPTY) return;

                        if (material == Material.WATER_PLANT || material == Material.REPLACEABLE_WATER_PLANT) {
                            TileEntity blockentity = prevState.hasTileEntity() ? world.getBlockEntity(pos) : null;
                            dropResources(prevState, world, pos, blockentity);
                        }
                        replace = true;
                        break;
                    }
                }
                if (prevBlock == block) {
                    replace = true;
                    break;
                }
            }

            if (replace) {
                setBlockVarying(world, pos, newBlock, unifiedSeed);
            }
        }
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        super.writeToNBT(compound);

        PBNBTHelper.writeNBTBlocks("blocks", blocks, compound);
        PBNBTHelper.writeNBTBlocks("blocksToReplace", blocksToReplace, compound);
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        super.readFromNBT(compound);

        blocks = PBNBTHelper.readNBTBlocks("blocks", compound);
        blocksToReplace = PBNBTHelper.readNBTBlocks("blocksToReplace", compound);
    }
}
