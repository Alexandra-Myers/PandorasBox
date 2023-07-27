/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.Level;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectEntitiesBuff extends PBEffectEntityBased
{
    public MobEffectInstance[] effects;
    public PBEffectEntitiesBuff() {}

    public PBEffectEntitiesBuff(int maxTicksAlive, double range, MobEffectInstance[] effects)
    {
        super(maxTicksAlive, range);

        this.effects = effects;
    }

    @Override
    public void affectEntity(Level world, PandorasBoxEntity box, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength)
    {
        for (MobEffectInstance effect : effects)
        {
            int prevDuration = Mth.floor(prevRatio * strength * effect.getDuration());
            int newDuration = Mth.floor(newRatio * strength * effect.getDuration());
            int duration = newDuration - prevDuration;

            if (duration > 0)
            {
                MobEffectInstance effectInstance = new MobEffectInstance(effect.getEffect(), duration, effect.getAmplifier(), effect.isAmbient(), effect.isVisible());
                Potion curEffect = new Potion(effectInstance);
                addPotionEffectDuration(entity, curEffect);
            }
        }
    }

    @Override
    public void writeToNBT(CompoundTag compound)
    {
        super.writeToNBT(compound);
        PBNBTHelper.writeNBTPotions("potions", effects, compound);
    }

    @Override
    public void readFromNBT(CompoundTag compound)
    {
        super.readFromNBT(compound);
        effects = PBNBTHelper.readNBTPotions("potions", compound);
    }
}
