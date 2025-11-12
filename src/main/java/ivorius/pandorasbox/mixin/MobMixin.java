package ivorius.pandorasbox.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import ivorius.pandorasbox.entitites.FunctionalGiant;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public class MobMixin {
    @WrapOperation(method = "getAttackBoundingBox", at = @At(value = "NEW", target = "(DDDDDD)Lnet/minecraft/world/phys/AABB;"))
    public AABB editInitForFunctionalGiantPilot(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, Operation<AABB> original, @Local(ordinal = 0) Entity vehicle) {
        if (vehicle instanceof FunctionalGiant) {
            AABB vehicleBB = vehicle.getBoundingBox();
            minY = Math.min(vehicleBB.minY, minY);
            maxY = Math.max(vehicleBB.maxY, maxY);
        }
        return original.call(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
