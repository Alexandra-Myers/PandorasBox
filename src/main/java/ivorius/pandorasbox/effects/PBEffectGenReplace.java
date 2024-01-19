/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

import static net.minecraft.world.level.block.Block.dropResources;

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
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, int pass, BlockPos pos, double range) {
        if (!world.isClientSide) {
            Block newBlock = blocks[random.nextInt(blocks.length)];
            BlockState prevState = world.getBlockState(pos);
            Block prevBlock = prevState.getBlock();
            boolean replace = false;
            for (Block block : blocksToReplace) {
                if (block == Blocks.WATER) {
                    FluidState fluidstate = world.getFluidState(pos);
                    if (fluidstate.canHydrate(world, pos, Blocks.SPONGE.defaultBlockState(), BlockPos.containing(effectCenter))) {
                        if (prevBlock instanceof BucketPickup bucketpickup && !bucketpickup.pickupBlock(getPlayer(world, entity), world, pos, prevState).isEmpty()) break;

                        if (!(prevState.getBlock() instanceof LiquidBlock)) {
                            if (!prevState.is(Blocks.KELP)
                                    && !prevState.is(Blocks.KELP_PLANT)
                                    && !prevState.is(Blocks.SEAGRASS)
                                    && !prevState.is(Blocks.TALL_SEAGRASS)) continue;

                            BlockEntity blockentity = prevState.hasBlockEntity() ? world.getBlockEntity(pos) : null;
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
    public void writeToNBT(CompoundTag compound) {
        super.writeToNBT(compound);

        PBNBTHelper.writeNBTBlocks("blocks", blocks, compound);
        PBNBTHelper.writeNBTBlocks("blocksToReplace", blocksToReplace, compound);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        blocks = PBNBTHelper.readNBTBlocks("blocks", compound);
        blocksToReplace = PBNBTHelper.readNBTBlocks("blocksToReplace", compound);
    }
}
