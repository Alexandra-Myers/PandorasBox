/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.math.IvMathHelper;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenPool extends PBEffectGenStructure
{
    public Block block;
    public Block platformBlock;
    public PBEffectGenPool() {}

    public PBEffectGenPool(int time, int maxX, int maxZ, int maxY, int startY, int unifiedSeed, Block block, Block platformBlock)
    {
        super(time, maxX, maxZ, maxY, startY, unifiedSeed);

        this.block = block;
        this.platformBlock = platformBlock;
    }

    @Override
    public void buildStructure(World world, PandorasBoxEntity entity, BlockPos currentPos, Random random, float prevRatio, float newRatio, int length, int width, int height, int originY, int originX, int originZ) {
        if(platformBlock == null) platformBlock = Blocks.QUARTZ_BLOCK;
        if(currentPos.getY() == originY) {
            setBlockSafe(world, currentPos, platformBlock.defaultBlockState());
        } else if(IvMathHelper.compareOffsets(currentPos.getX(), originX, length) || IvMathHelper.compareOffsets(currentPos.getZ(), originZ, width)) {
            setBlockSafe(world, currentPos, platformBlock.defaultBlockState());
        } else if (currentPos.getY() < originY + height) {
            setBlockSafe(world, currentPos, block.defaultBlockState());
        } else {
            setBlockToAirSafe(world, currentPos);
        }
    }

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);

        compound.putString("block", PBNBTHelper.storeBlockString(block));

        if (platformBlock != null)
            compound.putString("platformBlock", PBNBTHelper.storeBlockString(platformBlock));
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);

        block = PBNBTHelper.getBlock(compound.getString("block"));
        platformBlock = PBNBTHelper.getBlock(compound.getString("platformBlock"));
    }
}
