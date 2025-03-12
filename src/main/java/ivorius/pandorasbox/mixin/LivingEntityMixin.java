package ivorius.pandorasbox.mixin;

import ivorius.pandorasbox.init.MobEffectInit;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract boolean hasEffect(Holder<MobEffect> holder);

    @Shadow @Nullable public abstract MobEffectInstance getEffect(Holder<MobEffect> holder);

    @Shadow public abstract boolean hurt(DamageSource damageSource, float f);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "push", at = @At(value = "HEAD"), cancellable = true)
    public void crush(Entity entity, CallbackInfo ci) {
        if (hasEffect(MobEffectInit.SHRUNK)) {
            MobEffectInstance mobEffectInstance = getEffect(MobEffectInit.SHRUNK);
            if (mobEffectInstance.getAmplifier() > 0 && entity.getBoundingBox().intersects(getBoundingBox().minX, getBoundingBox().maxY, getBoundingBox().minZ, getBoundingBox().maxX, getBoundingBox().maxY, getBoundingBox().maxZ)) {
                hurt(damageSources().cramming(), 0.2F * mobEffectInstance.getAmplifier());
                ci.cancel();
            }
        }
    }
}
