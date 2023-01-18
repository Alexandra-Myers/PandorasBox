/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Created by lukas on 31.03.14.
 */
public abstract class PBEffectEntityBased extends PBEffectNormal
{
    public double range;

    public PBEffectEntityBased(int maxTicksAlive, double range)
    {
        super(maxTicksAlive);
        this.range = range;
    }

    @Override
    public void doEffect(World world, PandorasBoxEntity entity, Vec3d effectCenter, Random random, float prevRatio, float newRatio)
    {
        AxisAlignedBB bb = new AxisAlignedBB(effectCenter.x - range, effectCenter.y - range, effectCenter.z - range, effectCenter.x + range, effectCenter.y + range, effectCenter.z + range);
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

    public abstract void affectEntity(World world, PandorasBoxEntity box, Random random, LivingEntity entity, double newRatio, double prevRatio, double strength);

    @Override
    public void writeToNBT(CompoundNBT compound)
    {

        compound.putDouble("range", range);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {

        range = compound.getDouble("range");
    }
}
