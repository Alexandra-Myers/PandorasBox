package ivorius.pandorasbox.effects.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.PBNBTHelper;
import net.minecraft.core.registries.BuiltInRegistries;
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
    public static final Codec<MobEffectInstance> MOB_EFFECT_INSTANCE_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(BuiltInRegistries.MOB_EFFECT.byNameCodec().fieldOf("id").forGetter(MobEffectInstance::getEffect),
                            Codec.intRange(0, 255).optionalFieldOf("amplifier", 0).forGetter(MobEffectInstance::getAmplifier),
                            Codec.INT.optionalFieldOf("duration", 0).forGetter(MobEffectInstance::getDuration),
                            Codec.BOOL.optionalFieldOf("ambient", Boolean.FALSE).forGetter(MobEffectInstance::isAmbient),
                            Codec.BOOL.optionalFieldOf("show_particles", Boolean.TRUE).forGetter(MobEffectInstance::isVisible),
                            Codec.BOOL.optionalFieldOf("show_icon", Boolean.TRUE).forGetter(MobEffectInstance::showIcon))
                    .apply(instance, MobEffectInstance::new));
    public static final MapCodec<BuffEntityEffect> CODEC = PBNBTHelper.arrayCodec(MOB_EFFECT_INSTANCE_CODEC, () -> new MobEffectInstance[0]).fieldOf("effects").xmap(BuffEntityEffect::new, BuffEntityEffect::effects);
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
