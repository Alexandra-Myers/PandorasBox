/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.block.Block;
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
public class PBEffectGenPool extends PBEffectGenerate
{
    public Block block;
    public Block platformBlock;
    public PBEffectGenPool() {}

    public PBEffectGenPool(int time, double range, int unifiedSeed, Block block, Block platformBlock)
    {
        super(time, range, 1, unifiedSeed);

        this.block = block;
        this.platformBlock = platformBlock;
    }

    @Override
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range)
    {
        if (world instanceof ServerWorld)
        {
            if (world.loadedAndEntityCanStandOn(pos, entity))
            {
                boolean setPlatform = false;

                if (platformBlock != null)
                {
                    List<LivingEntity> entityList = world.getEntitiesOfClass(LivingEntity.class, BlockPositions.expandToAABB(pos, 2.5, 2.5, 2.5));

                    if (entityList.size() > 0)
                    {
                        setPlatform = true;
                    }
                }

                if (setPlatform)
                {
                    setBlockVarying(world, pos, platformBlock, unifiedSeed);
                }
                else
                {
                    setBlockVarying(world, pos, block, unifiedSeed);
                }
            }
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
