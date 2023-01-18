/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectEntitiesCrush extends PBEffectEntityBased
{
    public int cycles;
    public double speed;

    public PBEffectEntitiesCrush(int maxTicksAlive, double range, int cycles, double speed)
    {
        super(maxTicksAlive, range);
        this.cycles = cycles;
        this.speed = speed;
    }

    @Override
    public void affectEntity(World world, PandorasBoxEntity box, Random random, LivingEntity entity, double newRatio, double prevRatio, double strength)
    {
        boolean lift = ((newRatio * cycles) % 1.000001) < 0.7; // We want 1.0 inclusive

        if (lift)
        {
            double x = entity.getDeltaMovement().x;
            double y = entity.getDeltaMovement().y;
            double z = entity.getDeltaMovement().z;
            entity.getDeltaMovement().scale(0);
            entity.getDeltaMovement().add(x, y * (1.0f - strength) + strength * speed, z);
        }
        else
        {
            double x = entity.getDeltaMovement().x;
            double y = entity.getDeltaMovement().y;
            double z = entity.getDeltaMovement().z;
            entity.getDeltaMovement().scale(0);
            entity.getDeltaMovement().add(x, y - strength * speed, z);
        }
    }

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);

        compound.putInt("cycles", cycles);
        compound.putDouble("speed", speed);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);

        cycles = compound.getInt("cycles");
        speed = compound.getDouble("speed");
    }
}
