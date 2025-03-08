package ivorius.pandorasbox.client.rendering;

import com.mojang.blaze3d.vertex.BufferUploader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class FakeDeathOverlay extends Overlay {
    private final Minecraft minecraft;
    private final Screen screen;
    private final long startMillis;

    public FakeDeathOverlay(@NotNull DeathScreen deathScreen) {
        this.minecraft = Minecraft.getInstance();
        this.screen = deathScreen;
        this.screen.added();
        this.startMillis = Util.getMillis();

        BufferUploader.reset();
        screen.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        minecraft.noRender = false;
        minecraft.setOverlay(this);
    }
    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        float progress = (Util.getMillis() - startMillis) / 2500.0F;
        if (minecraft.screen != null) minecraft.screen.render(guiGraphics, i, j, f);
        screen.render(guiGraphics, i, j, f);
        if (progress >= 1) {
            screen.removed();
            minecraft.setOverlay(null);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
