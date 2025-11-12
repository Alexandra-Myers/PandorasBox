package ivorius.pandorasbox.client.rendering.effects.renderstate;

import net.minecraft.resources.ResourceLocation;

public class PandoraEffectRenderState {
    public ResourceLocation renderer;
    public int maxTicksAlive;
    public int effectTicksExisted;
    public boolean isDone;
    public boolean rendersAnyways;

    public boolean shouldRender(int boxDeathTicks) {
        return rendersAnyways || !(isDone || boxDeathTicks >= 0);
    }
}
