package ivorius.pandorasbox.mixin.client;

import ivorius.pandorasbox.PandorasBoxClient;
import ivorius.pandorasbox.client.rendering.FakeDeathOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow private @Nullable Overlay overlay;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;showDebugScreen()Z"))
    public void injectKeybinds(CallbackInfo ci) {
        if (overlay instanceof FakeDeathOverlay fakeDeathOverlay) fakeDeathOverlay.tick();
    }

    @Inject(method = "setOverlay", at = @At(value = "HEAD"), cancellable = true)
    public void updateOverlay(Overlay overlay, CallbackInfo ci) {
        if (overlay == null && PandorasBoxClient.cached != null) {
            this.overlay = PandorasBoxClient.cached;
            PandorasBoxClient.cached = null;
            ci.cancel();
        }
    }
}
