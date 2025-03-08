package ivorius.pandorasbox.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import ivorius.pandorasbox.client.rendering.FakeDeathOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow @Final private Minecraft minecraft;

    @ModifyExpressionValue(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getHealth()F"))
    public float fakePlayerHealth(float original) {
        if (minecraft.getOverlay() instanceof FakeDeathOverlay) return 0;
        return original;
    }
}
