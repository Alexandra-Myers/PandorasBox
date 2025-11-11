package ivorius.pandorasbox.effects.entity;

import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import static ivorius.pandorasbox.effects.PBEffect.combinedEffectDuration;

public record BuffEntityEffect(MobEffectInstance[] effects) implements EntityEffect {
    public static final MapCodec<BuffEntityEffect> CODEC = PBNBTHelper.arrayCodec(MobEffectInstance.CODEC, () -> new MobEffectInstance[0]).fieldOf("effects").xmap(BuffEntityEffect::new, BuffEntityEffect::effects);
    @Override
    public void affectEntity(Level level, PandorasBoxEntity box, Vec3 effectCenter, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {
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
    public void affectEntityServer(ServerLevel serverLevel, PandorasBoxEntity box, Vec3 effectCenter, RandomSource random, LivingEntity entity, double newRatio, double prevRatio, double strength) {

    }

    @Override
    public @NotNull MapCodec<? extends EntityEffect> codec() {
        return CODEC;
    }
}
