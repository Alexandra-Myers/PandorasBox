/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenHeightNoise extends PBEffectGenerate2D {
    public int minShift;
    public int maxShift;

    public int minTowerSize;
    public int maxTowerSize;

    public int blockSize;
    public PBEffectGenHeightNoise() {}

    public PBEffectGenHeightNoise(int time, double range, int unifiedSeed, int minShift, int maxShift, int minTowerSize, int maxTowerSize, int blockSize) {
        super(time, range, 1, unifiedSeed);

        this.minShift = minShift;
        this.maxShift = maxShift;

        this.minTowerSize = minTowerSize;
        this.maxTowerSize = maxTowerSize;

        this.blockSize = blockSize;
    }

    @Override
    public void generateOnSurface(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, BlockPos pos, double range, int pass) {
        if (!world.isClientSide) {
            int randomX = pos.getX() - (pos.getX() % blockSize);
            int randomZ = pos.getZ() - (pos.getZ() % blockSize);
            Random usedRandom = new Random(new Random(randomX).nextLong() ^ new Random(randomZ).nextLong());

            int shift = minShift + usedRandom.nextInt(maxShift - minShift + 1);
            int towerSize = minTowerSize + usedRandom.nextInt(maxTowerSize - minTowerSize + 1);

            int towerMinY = pos.getY() - towerSize / 2;
            int minEffectY = towerMinY + Math.min(0, shift);
            int maxEffectY = towerMinY + towerSize + Math.max(0, shift);

            List<PlayerEntity> entityList = world.getEntitiesOfClass(PlayerEntity.class, new AxisAlignedBB(pos.getX() - 2.0, minEffectY - 4, pos.getZ() - 3.0, pos.getX() + 4.0, maxEffectY + 4, pos.getZ() + 4.0));

            if (entityList.isEmpty()) {
                BlockState[] blockStates = new BlockState[towerSize];
                for (int y = 0; y < towerSize; y++)
                    blockStates[y] = world.getBlockState(pos.above(towerSize / 2 - y));

                for (int y = 0; y < towerSize; y++)
                    setBlockSafe(world, new BlockPos(pos.getX(), towerMinY + y, pos.getZ()), blockStates[y]);
            }
        }
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        super.writeToNBT(compound);

        compound.putInt("minShift", minShift);
        compound.putInt("maxShift", maxShift);
        compound.putInt("minTowerSize", minTowerSize);
        compound.putInt("maxTowerSize", maxTowerSize);
        compound.putInt("blockSize", blockSize);
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        super.readFromNBT(compound);

        minShift = compound.getInt("minShift");
        maxShift = compound.getInt("maxShift");
        minTowerSize = compound.getInt("minTowerSize");
        maxTowerSize = compound.getInt("maxTowerSize");
        blockSize = compound.getInt("blockSize");
    }
}
