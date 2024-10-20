/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectEntitiesCrush extends PBEffectEntityBased {
    public int cycles;
    public double speed;
    public PBEffectEntitiesCrush() {
    }

    public PBEffectEntitiesCrush(int maxTicksAlive, double range, int cycles, double speed) {
        super(maxTicksAlive, range);
        this.cycles = cycles;
        this.speed = speed;
    }

    @Override
    public void affectEntity(Level world, PandorasBoxEntity box, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
        boolean lift = ((newRatio * cycles) % 1.000001) < 0.7; // We want 1.0 inclusive

        double x = entity.getDeltaMovement().x;
        double y = entity.getDeltaMovement().y;
        double z = entity.getDeltaMovement().z;
        if (lift) {
            entity.setDeltaMovement(x, y * (1.0f - strength) + strength * speed, z);
        } else {
            entity.setDeltaMovement(x, y - strength * speed, z);
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound) {
        super.writeToNBT(compound);

        compound.putInt("cycles", cycles);
        compound.putDouble("speed", speed);
    }

    @Override
    public void readFromNBT(CompoundTag compound) {
        super.readFromNBT(compound);

        cycles = compound.getInt("cycles");
        speed = compound.getDouble("speed");
    }
}
