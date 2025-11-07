package ivorius.pandorasbox.client.rendering;

import ivorius.pandorasbox.client.rendering.effects.renderstate.PandoraEffectRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class PandorasBoxRenderState extends EntityRenderState {
    public float xRot;
    public float yRot;
    public float boxScale;
    public float partialTicks;
    public int entityTickCount;
    public int boxDeathTicks;
    public boolean invisibleToPlayer;
    public PandoraEffectRenderState pandoraEffectRenderState;
    public ItemStackRenderState renderItem = new ItemStackRenderState();
    static final PandorasBoxRenderState BLOCK_ENTITY_STATE;
    static {
        BLOCK_ENTITY_STATE = new PandorasBoxRenderState();
        BLOCK_ENTITY_STATE.xRot = (float) (-0.025F * 2F / 3F * Math.PI);
    }
}
