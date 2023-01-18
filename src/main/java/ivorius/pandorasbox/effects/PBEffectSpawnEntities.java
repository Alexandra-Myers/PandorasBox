/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.EntityPandorasBox;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public abstract class PBEffectSpawnEntities extends PBEffectNormal
{
    public int number;

    public boolean spawnFromBox;
    public double range;
    public double shiftY;
    public double throwStrengthSideMin;
    public double throwStrengthSideMax;
    public double throwStrengthYMin;
    public double throwStrengthYMax;

    public PBEffectSpawnEntities(int time, int number)
    {
        super(time);

        this.number = number;
    }

    public void setDoesNotSpawnFromBox(double range, double shiftY)
    {
        this.spawnFromBox = false;
        this.range = range;
        this.shiftY = shiftY;
    }

    public void setDoesSpawnFromBox(double throwStrengthSideMin, double throwStrengthSideMax, double throwStrengthYMin, double throwStrengthYMax)
    {
        this.spawnFromBox = true;
        this.throwStrengthSideMin = throwStrengthSideMin;
        this.throwStrengthSideMax = throwStrengthSideMax;
        this.throwStrengthYMin = throwStrengthYMin;
        this.throwStrengthYMax = throwStrengthYMax;
    }

    @Override
    public void doEffect(World world, EntityPandorasBox box, Vec3d effectCenter, Random random, float prevRatio, float newRatio)
    {
        if (world instanceof ServerWorld)
        {
            int prev = getSpawnNumber(prevRatio);
            int toSpawn = getSpawnNumber(newRatio) - prev;

            for (int i = 0; i < toSpawn; i++)
            {
                double eX;
                double eY;
                double eZ;

                if (spawnFromBox)
                {
                    eX = box.getX();
                    eY = box.getY();
                    eZ = box.getZ();
                }
                else
                {
                    eX = box.getX() + (random.nextDouble() - random.nextDouble()) * range;
                    eY = box.getY() + (random.nextDouble() - random.nextDouble()) * 3.0 + shiftY;
                    eZ = box.getZ() + (random.nextDouble() - random.nextDouble()) * range;
                }

                Entity newEntity = spawnEntity(world, box, random, prev + i, eX, eY, eZ);
                if (newEntity != null)
                {
                    if (spawnFromBox && !(newEntity instanceof LivingEntity))
                    {
                        // FIXME Disabled because it causes mobs to sink in the ground on clients (async) >.>
                        float dirSide = random.nextFloat() * 2.0f * 3.1415926f;
                        double throwStrengthSide = throwStrengthSideMin + random.nextDouble() * (throwStrengthSideMax - throwStrengthSideMin);

                        newEntity.push(MathHelper.sin(dirSide) * throwStrengthSide,
                                throwStrengthYMin + random.nextDouble() * (throwStrengthYMax - throwStrengthYMin),
                                MathHelper.cos(dirSide) * throwStrengthSide);
                        newEntity.hurtMarked = true;
                    }
                }
            }
        }
    }

    private int getSpawnNumber(float ratio)
    {
        return MathHelper.floor(ratio * number);
    }

    public abstract Entity spawnEntity(World world, EntityPandorasBox pbEntity, Random random, int number, double x, double y, double z);

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);

        compound.putInt("number", number);
        compound.putBoolean("spawnFromBox", spawnFromBox);
        compound.putDouble("range", range);
        compound.putDouble("shiftY", shiftY);
        compound.putDouble("throwStrengthSideMin", throwStrengthSideMin);
        compound.putDouble("throwStrengthSideMax", throwStrengthSideMax);
        compound.putDouble("throwStrengthYMin", throwStrengthYMin);
        compound.putDouble("throwStrengthYMax", throwStrengthYMax);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);

        number = compound.getInt("number");
        spawnFromBox = compound.getBoolean("spawnFromBox");
        range = compound.getDouble("range");
        shiftY = compound.getDouble("shiftY");
        throwStrengthSideMin = compound.getDouble("throwStrengthSideMin");
        throwStrengthSideMax = compound.getDouble("throwStrengthSideMax");
        throwStrengthYMin = compound.getDouble("throwStrengthYMin");
        throwStrengthYMax = compound.getDouble("throwStrengthYMax");
    }
}
