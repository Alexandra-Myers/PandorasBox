package ivorius.pandorasbox.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import ivorius.pandorasbox.client.rendering.FakeDeathOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {
    @ModifyExpressionValue(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isDown()Z"))
    public boolean hijackKeyboardIfFakedDeath(boolean original) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.getOverlay() instanceof FakeDeathOverlay) return false;
        return original;
    }
}
