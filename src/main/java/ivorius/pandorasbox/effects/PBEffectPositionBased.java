/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.position.PositionEffect;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectPositionBased extends PBEffectNormal {
    public static final MapCodec<PBEffectPositionBased> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(base(),
                            Codec.INT.fieldOf("number").forGetter(PBEffectPositionBased::getNumber),
                            Codec.DOUBLE.fieldOf("range").forGetter(PBEffectPositionBased::getRange),
                            PositionEffect.CODEC.fieldOf("effect").forGetter(PBEffectPositionBased::getEffect))
                    .apply(instance, PBEffectPositionBased::new));
    public final int number;

    public final double range;
    public final PositionEffect effect;

    public PBEffectPositionBased(int time, int number, double range, PositionEffect effect) {
        super(time);

        this.number = number;
        this.range = range;
        this.effect = effect;
    }

    public int getNumber() {
        return number;
    }

    public double getRange() {
        return range;
    }

    public PositionEffect getEffect() {
        return effect;
    }

    @Override
    public void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {
        if (level instanceof ServerLevel serverLevel) {
            int prev = getSpawnNumber(prevRatio);
            int toSpawn = getSpawnNumber(newRatio) - prev;

            for (int i = 0; i < toSpawn; i++) {
                double eX = effectCenter.x + (random.nextDouble() - random.nextDouble()) * range;
                double eY = effectCenter.y + (random.nextDouble() - random.nextDouble()) * 3.0 * 2.0;
                double eZ = effectCenter.z + (random.nextDouble() - random.nextDouble()) * range;

                effect.doEffect(serverLevel, entity, random, newRatio, prevRatio, eX, eY, eZ);
            }
        }
    }
    private int getSpawnNumber(float ratio) {
        return Mth.floor(ratio * number);
    }

    @Override
    public @NotNull MapCodec<? extends PBEffect> codec() {
        return CODEC;
    }
}
