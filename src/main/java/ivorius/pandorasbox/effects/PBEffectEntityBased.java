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
import net.minecraft.world.phys.AABB;

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
    public void doEffect(Level world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, float prevRatio, float newRatio)
    {
        AABB bb = new AABB(effectCenter.x - range, effectCenter.y - range, effectCenter.z - range, effectCenter.x + range, effectCenter.y + range, effectCenter.z + range);
        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, bb);

        for (LivingEntity entityLivingBase : entities)
        {
            double dist = entityLivingBase.distanceTo(entity);
            double strength = (range - dist) / range;

            if (strength > 0.0)
            {
                affectEntity(world, entity, random, entityLivingBase, newRatio, prevRatio, strength);
            }
        }
    }

    public abstract void affectEntity(Level world, PandorasBoxEntity box, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength);

    @Override
    public void writeToNBT(CompoundTag compound)
    {

        compound.putDouble("range", range);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {

        range = compound.getDouble("range");
    }
}
