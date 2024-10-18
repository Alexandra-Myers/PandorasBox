/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Created by lukas on 31.03.14.
 */
public abstract class PBEffectEntityBased extends PBEffectNormal
{
    public double range;
    public PBEffectEntityBased() {}

    public PBEffectEntityBased(int maxTicksAlive, double range)
    {
        super(maxTicksAlive);
        this.range = range;
    }

    @Override
    public void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {
        AABB bb = new AABB(effectCenter.x - range, effectCenter.y - range, effectCenter.z - range, effectCenter.x + range, effectCenter.y + range, effectCenter.z + range);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, bb);

        for (LivingEntity entityLivingBase : entities) {
            double dist = entityLivingBase.distanceTo(entity);
            double strength = (range - dist) / range;

            if (strength > 0.0) {
                affectEntity(level, entity, random, entityLivingBase, newRatio, prevRatio, strength);
            }
        }
    }

    public void affectEntity(Level level, PandorasBoxEntity box, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
        if (level instanceof ServerLevel serverLevel) affectEntityServer(serverLevel, box, random, entity, newRatio, prevRatio, strength);
    }

    public abstract void affectEntityServer(ServerLevel serverLevel, PandorasBoxEntity box, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength);

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess) {
        compound.putDouble("range", range);
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess) {
        range = compound.getDouble("range");
    }
}
