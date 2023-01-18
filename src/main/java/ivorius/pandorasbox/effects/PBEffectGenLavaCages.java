/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenLavaCages extends PBEffectGenerate
{
    public Block lavaBlock;
    public Block fillBlock;
    public Block cageBlock;

    public PBEffectGenLavaCages(int time, double range, int unifiedSeed, Block lavaBlock, Block cageBlock, Block fillBlock)
    {
        super(time, range, 1, unifiedSeed);

        this.lavaBlock = lavaBlock;
        this.cageBlock = cageBlock;
        this.fillBlock = fillBlock;
    }

    @Override
    public void generateOnBlock(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, int pass, BlockPos pos, double range)
    {
        if (world instanceof ServerWorld)
        {
            if (!world.loadedAndEntityCanStandOn(pos, entity))
            {
                List<PlayerEntity> innerList = world.getEntitiesOfClass(PlayerEntity.class, BlockPositions.expandToAABB(pos, (double) 2, (double) 2, (double) 2));

                if (innerList.size() == 0)
                {
                    List<PlayerEntity> outerList = world.getEntitiesOfClass(PlayerEntity.class, BlockPositions.expandToAABB(pos, 3.5, 3.5, 3.5));

                    if (outerList.size() > 0)
                    {
                        boolean isCage = true;

                        if (lavaBlock != null)
                        {
                            if (pos.getX() % 3 == 0 && pos.getZ() % 3 == 0 && pos.getY() % 3 == 0)
                            {
                                isCage = false;
                            }
                        }

                        if (isCage)
                        {
                            setBlockVarying(world, pos, cageBlock, unifiedSeed);
                        }
                        else
                        {
                            setBlockVarying(world, pos, lavaBlock, unifiedSeed);
                        }
                    }
                }
                else
                {
                    if (fillBlock != null)
                    {
                        setBlockVarying(world, pos, fillBlock, unifiedSeed);
                    }
                }
            }
        }
    }

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);

        if (lavaBlock != null)
            compound.putString("lavaBlock", PBNBTHelper.storeBlockString(lavaBlock));
        if (fillBlock != null)
            compound.putString("fillBlock", PBNBTHelper.storeBlockString(fillBlock));

        compound.putString("cageBlock", PBNBTHelper.storeBlockString(cageBlock));
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);

        lavaBlock = PBNBTHelper.getBlock(compound.getString("lavaBlock"));
        fillBlock = PBNBTHelper.getBlock(compound.getString("fillBlock"));
        cageBlock = PBNBTHelper.getBlock(compound.getString("cageBlock"));
    }
}
