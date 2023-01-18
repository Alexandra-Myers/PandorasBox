/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenTransform extends PBEffectGenerate
{
    public Block[] blocks;

    public PBEffectGenTransform(int time, double range, int unifiedSeed, Block[] blocks)
    {
        super(time, range, 1, unifiedSeed);

        this.blocks = blocks;
    }

    @Override
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range)
    {
        if (world instanceof ServerWorld)
        {
            Block block = blocks[random.nextInt(blocks.length)];

            if (world.loadedAndEntityCanStandOn(pos, entity))
            {
                setBlockVarying(world, pos, block, unifiedSeed);
            }
        }
    }

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);

        PBNBTHelper.writeNBTBlocks("blocks", blocks, compound);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);

        blocks = PBNBTHelper.readNBTBlocks("blocks", compound);
    }
}
