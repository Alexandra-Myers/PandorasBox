/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectEntitiesBuff extends PBEffectEntityBased {
    public EffectInstance[] effects;
    public PBEffectEntitiesBuff() {}

    public PBEffectEntitiesBuff(int maxTicksAlive, double range, EffectInstance[] effects) {
        super(maxTicksAlive, range);

        this.effects = effects;
    }

    @Override
    public void affectEntity(World world, PandorasBoxEntity box, Random random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
        EffectInstance[] effectsAdj = new EffectInstance[effects.length];
        for (int i = 0; i < effects.length; i++) {
            EffectInstance effect = effects[i];
            int prevDuration = MathHelper.floor(prevRatio * strength * effect.getDuration());
            int newDuration = MathHelper.floor(newRatio * strength * effect.getDuration());
            int duration = newDuration - prevDuration;

            if (duration > 0) {
                EffectInstance effectInstance = new EffectInstance(effect.getEffect(), duration, effect.getAmplifier(), effect.isAmbient(), effect.isVisible());
                effectsAdj[i] = effectInstance;
            }
        }
        combinedEffectDuration(entity, effectsAdj);
    }

    @Override
    public void writeToNBT(CompoundNBT compound) {
        super.writeToNBT(compound);
        PBNBTHelper.writeNBTPotions("potions", effects, compound);
    }

    @Override
    public void readFromNBT(CompoundNBT compound) {
        super.readFromNBT(compound);
        effects = PBNBTHelper.readNBTPotions("potions", compound);
    }
}
