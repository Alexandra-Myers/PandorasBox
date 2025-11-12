package ivorius.pandorasbox.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.GiantZombieModel;
import net.minecraft.world.entity.monster.Giant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GiantZombieModel.class)
public class GiantZombieModelMixin {
    @ModifyReturnValue(method = "isAggressive(Lnet/minecraft/world/entity/monster/Giant;)Z", at = @At("RETURN"))
    public boolean changeAggression(boolean original, @Local(ordinal = 0, argsOnly = true) Giant giant) {
        return original || giant.isAggressive();
    }
}