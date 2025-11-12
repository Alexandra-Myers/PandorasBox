package ivorius.pandorasbox.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import ivorius.pandorasbox.client.rendering.FakeDeathOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow
    @Final
    Minecraft minecraft;

    @ModifyExpressionValue(method = "bobHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isDeadOrDying()Z"))
    public boolean modifyDeathCheck(boolean original) {
        return original || minecraft.getOverlay() instanceof FakeDeathOverlay;
    }
}
