/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 30.03.14.
 */
public abstract class PBEffectNormal extends PBEffect {
    public int maxTicksAlive;
    public PBEffectNormal() {}

    public PBEffectNormal(int maxTicksAlive) {
        this.maxTicksAlive = maxTicksAlive;
    }

    public float getRatioDone(int ticks) {
        if (ticks == maxTicksAlive) { // Make sure value is exact
            return 1.0f;
        }

        return (float) ticks / (float) maxTicksAlive;
    }

    public abstract void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio);

    public void setUpEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random) {

    }

    public void finalizeEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random) {

    }

    @Override
    public void doTick(PandorasBoxEntity entity, Vec3 effectCenter, int ticksAlive) {
        float prevRatio = getRatioDone(ticksAlive);
        float newRatio = getRatioDone(ticksAlive + 1);

        if (ticksAlive == 0)
            setUpEffect(entity.level(), entity, effectCenter, entity.getRandom());

        if (prevRatio >= 0.0f && newRatio <= 1.0f && newRatio > prevRatio)
            doEffect(entity.level(), entity, effectCenter, entity.getRandom(), prevRatio, newRatio);

        if (ticksAlive == maxTicksAlive - 1)
            finalizeEffect(entity.level(), entity, effectCenter, entity.getRandom());
    }

    @Override
    public boolean isDone(PandorasBoxEntity entity, int ticksAlive) {
        return ticksAlive >= maxTicksAlive;
    }

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess) {
        compound.putInt("maxTicksAlive", maxTicksAlive);
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess) {
        maxTicksAlive = compound.getInt("maxTicksAlive");
    }

    @Override
    public boolean canGenerateMoreEffectsAfterwards(PandorasBoxEntity entity) {
        return true;
    }
}
