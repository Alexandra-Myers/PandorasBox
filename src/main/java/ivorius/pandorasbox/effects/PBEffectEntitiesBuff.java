/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.EntityPandorasBox;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectEntitiesBuff extends PBEffectEntityBased
{
    public EffectInstance[] effects;

    public PBEffectEntitiesBuff(int maxTicksAlive, double range, EffectInstance[] effects)
    {
        super(maxTicksAlive, range);

        this.effects = effects;
    }

    @Override
    public void affectEntity(World world, EntityPandorasBox box, Random random, LivingEntity entity, double newRatio, double prevRatio, double strength)
    {
        for (EffectInstance effect : effects)
        {
            int prevDuration = MathHelper.floor(prevRatio * strength * effect.getDuration());
            int newDuration = MathHelper.floor(newRatio * strength * effect.getDuration());
            int duration = newDuration - prevDuration;

            if (duration > 0)
            {
                EffectInstance effectInstance = new EffectInstance(effect.getEffect(), duration, effect.getAmplifier(), effect.isAmbient(), effect.isVisible());
                Potion curEffect = new Potion(effectInstance);
                addPotionEffectDuration(entity, curEffect);
            }
        }
    }

    @Override
    public void writeToNBT(CompoundNBT compound)
    {
        super.writeToNBT(compound);
        PBNBTHelper.writeNBTPotions("potions", effects, compound);
    }

    @Override
    public void readFromNBT(CompoundNBT compound)
    {
        super.readFromNBT(compound);
        effects = PBNBTHelper.readNBTPotions("potions", compound);
    }
}
