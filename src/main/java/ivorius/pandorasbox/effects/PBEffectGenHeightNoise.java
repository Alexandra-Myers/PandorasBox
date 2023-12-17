/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenHeightNoise extends PBEffectGenerate2D
{
    public int minShift;
    public int maxShift;

    public int minTowerSize;
    public int maxTowerSize;

    public int blockSize;
    public PBEffectGenHeightNoise() {}

    public PBEffectGenHeightNoise(int time, double range, int unifiedSeed, int minShift, int maxShift, int minTowerSize, int maxTowerSize, int blockSize)
    {
        super(time, range, 1, unifiedSeed);

        this.minShift = minShift;
        this.maxShift = maxShift;

        this.minTowerSize = minTowerSize;
        this.maxTowerSize = maxTowerSize;

        this.blockSize = blockSize;
    }

    @Override
    public void generateOnSurface(Level world, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, BlockPos pos, double range, int pass) {
        if (!world.isClientSide()) {
            int randomX = pos.getX() - (pos.getX() % blockSize);
            int randomZ = pos.getZ() - (pos.getZ() % blockSize);
            Random usedRandom = new Random(new Random(randomX).nextLong() ^ new Random(randomZ).nextLong());

            int shift = minShift + usedRandom.nextInt(maxShift - minShift + 1);
            int towerSize = minTowerSize + usedRandom.nextInt(maxTowerSize - minTowerSize + 1);

            int towerMinY = pos.getY() - towerSize / 2;
            int minEffectY = towerMinY + Math.min(0, shift);
            int maxEffectY = towerMinY + towerSize + Math.max(0, shift);

            List<Player> entityList = world.getEntitiesOfClass(Player.class, new AABB(pos.getX() - 2.0, minEffectY - 4, pos.getZ() - 3.0, pos.getX() + 4.0, maxEffectY + 4, pos.getZ() + 4.0));

            if (entityList.isEmpty()) {
                for (int y = 0; y < towerSize; y++) {
                    BlockPos newPos = new BlockPos(pos.getX(), towerMinY, pos.getZ());
                    setBlockSafe(world, newPos.above(y), world.getBlockState(newPos.above(pos.getY() - towerMinY)));
                }
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);

        compound.putInt("minShift", minShift);
        compound.putInt("maxShift", maxShift);
        compound.putInt("minTowerSize", minTowerSize);
        compound.putInt("maxTowerSize", maxTowerSize);
        compound.putInt("blockSize", blockSize);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);

        minShift = compound.getInt("minShift");
        maxShift = compound.getInt("maxShift");
        minTowerSize = compound.getInt("minTowerSize");
        maxTowerSize = compound.getInt("maxTowerSize");
        blockSize = compound.getInt("blockSize");
    }
}
