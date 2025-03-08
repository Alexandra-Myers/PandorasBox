/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Created by lukas on 30.03.14.
 */
public abstract class PBEffectNormal extends PBEffect {
    public final int maxTicksAlive;

    public PBEffectNormal(int maxTicksAlive) {
        this.maxTicksAlive = maxTicksAlive;
    }

    public int getMaxTicksAlive() {
        return maxTicksAlive;
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
    public boolean isDone(int ticksAlive) {
        return ticksAlive >= maxTicksAlive;
    }

    @Override
    public boolean canGenerateMoreEffectsAfterwards(PandorasBoxEntity entity) {
        return true;
    }

    @Override
    public int getTicksExistedForEffect(PBEffect identityEffect, int ticksAlive) {
        return identityEffect == this ? ticksAlive : -1;
    }

    protected static <T extends PBEffectNormal> RecordCodecBuilder<T, Integer> base() {
        return Codec.INT.fieldOf("max_ticks_alive").forGetter(PBEffectNormal::getMaxTicksAlive);
    }
}
