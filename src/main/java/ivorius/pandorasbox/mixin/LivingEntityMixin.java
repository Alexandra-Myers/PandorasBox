package ivorius.pandorasbox.mixin;

import ivorius.pandorasbox.entitites.FunctionalGiant;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static ivorius.pandorasbox.PandorasBoxHelper.getMaxPosition;
import static ivorius.pandorasbox.PandorasBoxHelper.getMinPosition;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "push", at = @At(value = "HEAD"), cancellable = true)
    public void crush(Entity entity, CallbackInfo ci) {
        if (entity instanceof FunctionalGiant && crushThisEntityUnderWeight(entity, 5)) ci.cancel();
    }

    @Unique
    public boolean crushThisEntityUnderWeight(Entity causingEntity, int amplifier) {
        AABB causingOriginalBox = causingEntity.getBoundingBox();
        AABB thisOriginalBox = getBoundingBox();
        double thisYDiff = thisOriginalBox.maxY - thisOriginalBox.minY;
        double causalYDiff = causingOriginalBox.maxY - causingOriginalBox.minY;
        AABB flattened = new AABB(getMinPosition(causingOriginalBox), getMaxPosition(causingOriginalBox).subtract(0, causalYDiff * 0.9, 0));
        if (flattened.intersects(getMinPosition(thisOriginalBox).add(0, thisYDiff * 0.9, 0), getMaxPosition(thisOriginalBox))) {
            hurt(damageSources().cramming(), 0.2F * amplifier);
            return true;
        }
        return false;
    }
}
