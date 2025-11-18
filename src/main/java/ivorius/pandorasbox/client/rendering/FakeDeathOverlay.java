package ivorius.pandorasbox.client.rendering;

import com.mojang.blaze3d.vertex.BufferUploader;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class FakeDeathOverlay extends Overlay {
    private final Minecraft minecraft;
    private final Screen screen;
    private final long startMillis;
    private boolean wasMouseReleased;

    public FakeDeathOverlay(@NotNull DeathScreen deathScreen) {
        this.minecraft = Minecraft.getInstance();
        this.screen = deathScreen;
        this.screen.added();
        this.startMillis = Util.getMillis();
        if (minecraft.mouseHandler.isMouseGrabbed()) {
            wasMouseReleased = true;
            minecraft.mouseHandler.releaseMouse();
        }
        KeyMapping.releaseAll();

        BufferUploader.reset();
        screen.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        minecraft.noRender = false;
        minecraft.setOverlay(this);
    }

    public void tick() {
        screen.tick();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        float progress = (Util.getMillis() - startMillis) / 10000.0F;
        if (minecraft.screen != null) minecraft.screen.render(guiGraphics, i, j, f);
        screen.render(guiGraphics, i, j, f);
        if (progress >= 0.7) {
            guiGraphics.drawCenteredString(minecraft.font, Component.translatable("text.pandorasbox.fake", minecraft.getUser().getName()), screen.width / 2, 85, 16777215);
        }
        if (progress >= 1) {
            if (wasMouseReleased) minecraft.mouseHandler.grabMouse();
            screen.removed();
            if (minecraft.player != null) minecraft.player.deathTime = 0;
            minecraft.setOverlay(null);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
