package ivorius.pandorasbox.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import ivorius.pandorasbox.client.rendering.FakeDeathOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ForgeGui.class)
public class GuiMixin extends Gui {
    public GuiMixin(Minecraft arg, ItemRenderer arg2) {
        super(arg, arg2);
    }

    @ModifyExpressionValue(method = "renderHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getHealth()F"))
    public float fakePlayerHealth(float original) {
        if (minecraft.getOverlay() instanceof FakeDeathOverlay) return 0;
        return original;
    }
}
