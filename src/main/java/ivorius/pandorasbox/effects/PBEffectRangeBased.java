/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.math.IvMathHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 31.03.14.
 */
public abstract class PBEffectRangeBased extends PBEffectNormal {
    public double range;
    public int passes;
    public PBEffectRangeBased() {}

    public boolean spreadSquared = true;
    public boolean easeInOut = true;

    public PBEffectRangeBased(int maxTicksAlive, double range, int passes) {
        super(maxTicksAlive);
        this.range = range;
        this.passes = passes;
    }

    @Override
    public void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {
        for (int i = 0; i < passes; i++) {
            double prevRange = getRange(prevRatio, i);
            double newRange = getRange(newRatio, i);

            generateInRange(level, entity, random, effectCenter, prevRange, newRange, i);
        }
    }

    private double getRange(double ratio, int pass) {
        if (spreadSquared)
            ratio = Math.sqrt(ratio);
        if (easeInOut)
            ratio = IvMathHelper.mixEaseInOut(0.0, 1.0, ratio);

        double fullRange = range + (passes - 1) * 5.0;
        double tempRange = ratio * fullRange - pass * 5.0;

        return Mth.clamp(tempRange, 0.0, range);
    }

    public abstract void generateInRange(Level level, PandorasBoxEntity entity, RandomSource random, Vec3 effectCenter, double prevRange, double newRange, int pass);

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.writeToNBT(compound, registryAccess);

        compound.putDouble("range", range);
        compound.putInt("passes", passes);
        compound.putBoolean("spreadSquared", spreadSquared);
        compound.putBoolean("easeInOut", easeInOut);
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess)
    {
        super.readFromNBT(compound, registryAccess);

        range = compound.getDouble("range");
        passes = compound.getInt("passes");
        spreadSquared = compound.getBoolean("spreadSquared");
        easeInOut = compound.getBoolean("easeInOut");
    }
}
