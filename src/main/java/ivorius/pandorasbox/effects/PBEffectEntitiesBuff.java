/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectEntitiesBuff extends PBEffectEntityBased {
    public MobEffectInstance[] effects;
    public PBEffectEntitiesBuff() {}

    public PBEffectEntitiesBuff(int maxTicksAlive, double range, MobEffectInstance[] effects) {
        super(maxTicksAlive, range);

        this.effects = effects;
    }

    @Override
    public void affectEntity(Level level, PandorasBoxEntity box, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
        MobEffectInstance[] effectsAdj = new MobEffectInstance[effects.length];
        for (int i = 0; i < effects.length; i++) {
            MobEffectInstance effect = effects[i];
            int prevDuration = Mth.floor(prevRatio * strength * effect.getDuration());
            int newDuration = Mth.floor(newRatio * strength * effect.getDuration());
            int duration = newDuration - prevDuration;

            if (duration > 0) {
                MobEffectInstance effectInstance = new MobEffectInstance(effect.getEffect(), duration, effect.getAmplifier(), effect.isAmbient(), effect.isVisible());
                effectsAdj[i] = effectInstance;
            }
        }
        combinedEffectDuration(entity, effectsAdj);
    }

    @Override
    public void affectEntityServer(ServerLevel serverLevel, PandorasBoxEntity box, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {

    }

    @Override
    public void writeToNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.writeToNBT(compound, registryAccess);
        PBNBTHelper.writeNBTPotions("potions", effects, compound);
    }

    @Override
    public void readFromNBT(CompoundTag compound, RegistryAccess registryAccess) {
        super.readFromNBT(compound, registryAccess);
        effects = PBNBTHelper.readNBTPotions("potions", compound);
    }
}
