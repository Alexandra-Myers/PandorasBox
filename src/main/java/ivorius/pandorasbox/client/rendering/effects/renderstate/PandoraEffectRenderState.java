package ivorius.pandorasbox.client.rendering.effects.renderstate;

import net.minecraft.resources.Identifier;

public class PandoraEffectRenderState {
    public Identifier renderer;
    public int maxTicksAlive;
    public int effectTicksExisted;
    public boolean isDone;
    public boolean rendersAnyways;

    public boolean shouldRender(int boxDeathTicks) {
        return rendersAnyways || !(isDone || boxDeathTicks >= 0);
    }
}
