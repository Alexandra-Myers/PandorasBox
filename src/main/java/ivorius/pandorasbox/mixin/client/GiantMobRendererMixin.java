package ivorius.pandorasbox.mixin.client;

import net.minecraft.client.renderer.entity.GiantMobRenderer;
import net.minecraft.client.renderer.entity.state.ZombieRenderState;
import net.minecraft.world.entity.monster.Giant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GiantMobRenderer.class)
public class GiantMobRendererMixin {
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/monster/Giant;Lnet/minecraft/client/renderer/entity/state/ZombieRenderState;F)V", at = @At(value = "TAIL"))
    public void addAggressiveExtract(Giant giant, ZombieRenderState zombieRenderState, float f, CallbackInfo ci) {
        zombieRenderState.isAggressive = giant.isAggressive();
    }
}