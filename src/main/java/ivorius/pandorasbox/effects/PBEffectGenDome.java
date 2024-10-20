/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenDome extends PBEffectGenerate2D
{
    public Block block;
    public Block fillBlock;
    public PBEffectGenDome() {}

    public PBEffectGenDome(int time, double range, int unifiedSeed, Block block, Block fillBlock)
    {
        super(time, range, 2, unifiedSeed);
        this.block = block;
        this.fillBlock = fillBlock;
    }

    @Override
    public void generateOnSurface(Level world, PandorasBoxEntity box, Vec3 effectCenter, RandomSource random, BlockPos pos, double dist, int pass) {
        if (!world.isClientSide) {
            int domeHeightY = Mth.ceil(range);

            for (int y = -domeHeightY; y <= domeHeightY; y++) {
                BlockPos shiftedPos = pos.above(y);

                if (pass == 0) {
                    if (isSpherePart(shiftedPos.getX() + 0.5, shiftedPos.getY() + 0.5, shiftedPos.getZ() + 0.5, effectCenter.x, effectCenter.y, effectCenter.z, range - 1.5, range)) {
                        if (world.getBlockState(shiftedPos).canSurvive(world, shiftedPos)) {
                            setBlockVarying(world, shiftedPos, this.block, unifiedSeed);
                        }
                    }
                } else if (pass == 1 && fillBlock != null) {
                    if (isSpherePart(shiftedPos.getX() + 0.5, shiftedPos.getY() + 0.5, shiftedPos.getZ() + 0.5, effectCenter.x, effectCenter.y, effectCenter.z, 0.0, range - 1.5)) {
                        if (world.getBlockState(shiftedPos).canSurvive(world, shiftedPos)) {
                            setBlockVarying(world, shiftedPos, this.fillBlock, unifiedSeed);
                        }
                    }
                }
            }
        }
    }

    public static boolean isSpherePart(double x, double y, double z, double centerX, double centerY, double centerZ, double distStart, double distEnd)
    {
        double xDist = centerX - x;
        double yDist = centerY - y;
        double zDist = centerZ - z;
        double rangeSQ = xDist * xDist + yDist * yDist + zDist * zDist;

        return rangeSQ >= distStart * distStart && rangeSQ < distEnd * distEnd;
    }

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess)
    {
        super.writeToNBT(compound, registryAccess);

        compound.putString("block", PBNBTHelper.storeBlockString(block));

        if (fillBlock != null)
            compound.putString("fillBlock", PBNBTHelper.storeBlockString(fillBlock));
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess)
    {
        super.readFromNBT(compound, registryAccess);

        block = PBNBTHelper.getBlock(compound.getString("block"));
        if (compound.contains("fillBlock")) fillBlock = PBNBTHelper.getBlock(compound.getString("fillBlock"));
    }
}
