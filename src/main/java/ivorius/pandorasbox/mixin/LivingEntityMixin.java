package ivorius.pandorasbox.mixin;

import ivorius.pandorasbox.entitites.FunctionalGiant;
import ivorius.pandorasbox.init.MobEffectInit;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow public abstract boolean hasEffect(Holder<MobEffect> holder);

    @Shadow public abstract boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float f);

    @Shadow @Nullable public abstract MobEffectInstance getEffect(Holder<MobEffect> holder);

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "push", at = @At(value = "HEAD"), cancellable = true)
    public void crush(Entity entity, CallbackInfo ci) {
        if (entity instanceof FunctionalGiant && crushThisEntityUnderWeight(entity, 5)) ci.cancel();
        if (hasEffect(MobEffectInit.SHRUNK)) {
            MobEffectInstance mobEffectInstance = getEffect(MobEffectInit.SHRUNK);
            if (mobEffectInstance.getAmplifier() > 0 && crushThisEntityUnderWeight(entity, mobEffectInstance.getAmplifier())) ci.cancel();
        }
    }

    @Unique
    public boolean crushThisEntityUnderWeight(Entity causingEntity, int amplifier) {
        AABB causingOriginalBox = causingEntity.getBoundingBox();
        AABB thisOriginalBox = getBoundingBox();
        double thisYDiff = thisOriginalBox.maxY - thisOriginalBox.minY;
        double causalYDiff = causingOriginalBox.maxY - causingOriginalBox.minY;
        AABB flattened = new AABB(causingOriginalBox.getMinPosition(), causingOriginalBox.getMaxPosition().subtract(0, causalYDiff * 0.9, 0));
        if (flattened.intersects(thisOriginalBox.getMinPosition().add(0, thisYDiff * 0.9, 0), causingOriginalBox.getMaxPosition())) {
            if (level() instanceof ServerLevel serverLevel) hurtServer(serverLevel, damageSources().cramming(), 0.2F * amplifier);
            return true;
        }
        return false;
    }
}
