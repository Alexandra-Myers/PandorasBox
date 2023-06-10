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

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenTransform extends PBEffectGenerate
{
    public Block[] blocks;
    public PBEffectGenTransform() {}

    public PBEffectGenTransform(int time, double range, int unifiedSeed, Block[] blocks)
    {
        super(time, range, 1, unifiedSeed);

        this.blocks = blocks;
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, int pass, BlockPos pos, double range)
    {
        if (world instanceof ServerLevel)
        {
            Block block = blocks[random.nextInt(blocks.length)];

            if (world.loadedAndEntityCanStandOn(pos, entity))
            {
                setBlockVarying(world, pos, block, unifiedSeed);
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);

        PBNBTHelper.writeNBTBlocks("blocks", blocks, compound);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        blocks = PBNBTHelper.readNBTBlocks("blocks", compound);
    }
}
