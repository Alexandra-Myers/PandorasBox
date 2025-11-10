/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.entity.EntityEffect;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by lukas on 31.03.14.
 */
public class PBEffectEntityBased extends PBEffectNormal {
    public static final MapCodec<PBEffectEntityBased> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(base(),
                            Codec.DOUBLE.fieldOf("range").forGetter(PBEffectEntityBased::getRange),
                            EntityEffect.CODEC.fieldOf("effect").forGetter(PBEffectEntityBased::getEffect))
                    .apply(instance, PBEffectEntityBased::new));
    public final EntityEffect effect;
    public final double range;

    public PBEffectEntityBased(int maxTicksAlive, double range, EntityEffect effect) {
        super(maxTicksAlive);
        this.effect = effect;
        this.range = range;
    }

    public EntityEffect getEffect() {
        return effect;
    }

    public double getRange() {
        return range;
    }

    @Override
    public void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {
        AABB bb = new AABB(effectCenter.x - range, effectCenter.y - range, effectCenter.z - range, effectCenter.x + range, effectCenter.y + range, effectCenter.z + range);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, bb);

        for (LivingEntity entityLivingBase : entities) {
            double dist = entityLivingBase.distanceTo(entity);
            double strength = (range - dist) / range;

            if (strength > 0.0) {
                effect.affectEntity(level, entity, effectCenter, random, entityLivingBase, newRatio, prevRatio, strength);
            }
        }
    }

    @Override
    public void finalizeEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random) {
        effect.finalise(level, entity, effectCenter, random);
    }

    @Override
    public @NotNull MapCodec<? extends PBEffect> codec() {
        return CODEC;
    }
}
