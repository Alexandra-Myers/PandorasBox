/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.spawn_entities.SpawnEntitiesEffect;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectSpawnEntities extends PBEffectNormal {
    public static final MapCodec<PBEffectSpawnEntities> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(base(),
                            Codec.INT.fieldOf("number").forGetter(PBEffectSpawnEntities::getNumber),
                            SpawnEntitiesEffect.CODEC.fieldOf("effect").forGetter(PBEffectSpawnEntities::getEffect))
                    .apply(instance, PBEffectSpawnEntities::new));
    public final int number;
    public final SpawnEntitiesEffect effect;

    public PBEffectSpawnEntities(int time, int number, SpawnEntitiesEffect effect) {
        super(time);

        this.number = number;
        this.effect = effect;
    }

    @Override
    public void doEffect(Level level, PandorasBoxEntity box, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {
        if (!level.isClientSide()) {
            int prev = getSpawnNumber(prevRatio);
            int toSpawn = getSpawnNumber(newRatio) - prev;

            for (int i = 0; i < toSpawn; i++) {
                double eX;
                double eY;
                double eZ;

                Vec3 baseVec;
                if (effect.spawnsFromBox()) baseVec = box.position();
                else baseVec = effectCenter;

                if (effect.spawnDirect()) {
                    eX = baseVec.x;
                    eY = baseVec.y;
                    eZ = baseVec.z;
                } else {
                    eX = baseVec.x + (random.nextDouble() - random.nextDouble()) * effect.range();
                    eY = baseVec.y + (random.nextDouble() - random.nextDouble()) * 3.0 + effect.shiftY();
                    eZ = baseVec.z + (random.nextDouble() - random.nextDouble()) * effect.range();
                }

                Entity newEntity = effect.spawnEntity(level, box, random, prev + i, eX, eY, eZ);
                if (newEntity != null) {
                    if (effect.spawnDirect() && !(newEntity instanceof LivingEntity)) {
                        // FIXME Disabled because it causes mobs to sink in the ground on clients (async) >.>
                        float dirSide = random.nextFloat() * 2.0f * 3.1415926f;
                        double throwStrengthSide = effect.throwStrengthSideMin() + random.nextDouble() * (effect.throwStrengthSideMax() - effect.throwStrengthSideMin());

                        newEntity.push(Mth.sin(dirSide) * throwStrengthSide,
                                effect.throwStrengthYMin() + random.nextDouble() * (effect.throwStrengthYMax() - effect.throwStrengthYMin()),
                                Mth.cos(dirSide) * throwStrengthSide);
                        newEntity.hurtMarked = true;
                    }
                }
            }
        }
    }

    public SpawnEntitiesEffect getEffect() {
        return effect;
    }

    public int getNumber() {
        return number;
    }

    private int getSpawnNumber(float ratio) {
        return Mth.floor(ratio * number);
    }

    @Override
    public @NotNull MapCodec<? extends PBEffect> codec() {
        return CODEC;
    }
}
