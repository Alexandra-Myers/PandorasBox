/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Created by lukas on 31.03.14.
 */
public class PBEffectMulti extends PBEffect {
    public static final MapCodec<PBEffectMulti> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(PBNBTHelper.arrayCodec(PBEffect.CODEC, () -> new PBEffect[0]).fieldOf("effects").forGetter(PBEffectMulti::getEffects),
                            PBNBTHelper.arrayCodec(Codec.INT, () -> new Integer[0]).fieldOf("delays").forGetter(pbEffectMulti -> Arrays.stream(pbEffectMulti.getDelays()).boxed().toArray(Integer[]::new)))
                    .apply(instance, PBEffectMulti::new));
    public final PBEffect[] effects;
    public final int[] delays;

    public PBEffectMulti(PBEffect[] effects, int[] delays) {
        this.effects = effects;
        this.delays = delays;
    }
    public PBEffectMulti(PBEffect[] effects, Integer[] delays) {
        this(effects, Arrays.stream(delays).mapToInt(Integer::intValue).toArray());
    }

    public PBEffect[] getEffects() {
        return effects;
    }

    public int[] getDelays() {
        return delays;
    }

    @Override
    public void doTick(PandorasBoxEntity entity, Vec3 effectCenter, int ticksAlive) {
        for (int i = 0; i < effects.length; i++) {
            int effectTicks = ticksAlive - delays[i];
            effects[i].doTick(entity, effectCenter, effectTicks);
        }
    }

    @Override
    public boolean isDone(int ticksAlive) {
        for (int i = 0; i < effects.length; i++) {
            int effectTicks = ticksAlive - delays[i];
            if (!effects[i].isDone(effectTicks)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canGenerateMoreEffectsAfterwards(PandorasBoxEntity entity) {
        for (PBEffect effect : effects) {
            if (!effect.canGenerateMoreEffectsAfterwards(entity)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int getTicksExistedForEffect(PBEffect identityEffect, int ticksAlive) {
        for (int i = 0; i < effects.length; i++) {
            if (effects[i] == identityEffect) return ticksAlive - delays[i];
        }
        return -1;
    }

    @Override
    public int getMaxTicksAlive() {
        int highestLength = 0;
        for (int i = 0; i < effects.length; i++) {
            int effectTicks = effects[i].getMaxTicksAlive() + delays[i];
            if (effectTicks >= highestLength) highestLength = effectTicks;
        }
        return highestLength;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffect> codec() {
        return CODEC;
    }
}
