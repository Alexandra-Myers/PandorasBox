/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.math.IvMathHelper;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

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
        super(time, maxX, maxZ, maxY, startY, unifiedSeed, false);

        this.block = block;
        this.platformBlock = platformBlock;
    }

    @Override
    public void buildStructure(Level world, PandorasBoxEntity entity, BlockPos currentPos, RandomSource random, float prevRatio, float newRatio, int length, int width, int height, int originY, int originX, int originZ) {
        if(platformBlock == null) platformBlock = Blocks.QUARTZ_BLOCK;
        ServerLevel serverLevel = (ServerLevel) world;
        if (currentPos.getY() == originY) {
            setBlockSafe(serverLevel, currentPos, platformBlock.defaultBlockState());
        } else if (IvMathHelper.compareOffsets(currentPos.getX(), originX, length) || IvMathHelper.compareOffsets(currentPos.getZ(), originZ, width)) {
            setBlockSafe(serverLevel, currentPos, platformBlock.defaultBlockState());
        } else if (currentPos.getY() < originY + height) {
            setBlockSafe(serverLevel, currentPos, block.defaultBlockState());
        } else {
            setBlockToAirSafe(world, currentPos);
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);

        compound.putString("block", PBNBTHelper.storeBlockString(block));

        if (platformBlock != null)
            compound.putString("platformBlock", PBNBTHelper.storeBlockString(platformBlock));
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        block = PBNBTHelper.getBlock(compound.getString("block"));
        platformBlock = PBNBTHelper.getBlock(compound.getString("platformBlock"));
    }
    enum Mode {
        REPLACE((p_198450_0_, p_198450_1_, p_198450_2_, p_198450_3_) -> p_198450_2_);

        public final SetBlockCommand.Filter filter;

        Mode(SetBlockCommand.Filter p_i47985_3_) {
            this.filter = p_i47985_3_;
        }
    }
}
