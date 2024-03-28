/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectSpawnBlocks extends PBEffectSpawnEntities {
    public Block[] blocks;
    public PBEffectSpawnBlocks() {}

    public PBEffectSpawnBlocks(int time, Block[] blocks) {
        super(time, blocks.length);

        setDoesSpawnFromBox(0.1, 0.4, 0.2, 1.0);
        this.blocks = blocks;
    }

    public PBEffectSpawnBlocks(int time, double range, double shiftY, Block[] blocks) {
        super(time, blocks.length);

        setDoesNotSpawnFromBox(range, shiftY);
        this.blocks = blocks;
    }

    @Override
    public Entity spawnEntity(World world, PandorasBoxEntity entity, Random random, int number, double x, double y, double z) {
        if(world.isClientSide()) return null;
        Block block = blocks[number];
        FallingBlockEntity entityFallingBlock = new FallingBlockEntity(world, x, y, z, block.defaultBlockState());
        entityFallingBlock.time = 1;
        world.addFreshEntity(entityFallingBlock);

        return entityFallingBlock;
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
