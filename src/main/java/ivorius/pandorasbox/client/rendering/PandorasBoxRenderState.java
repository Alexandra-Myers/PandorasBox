package ivorius.pandorasbox.client.rendering;

import ivorius.pandorasbox.effects.PBEffect;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class PandorasBoxRenderState extends EntityRenderState {
    public float xRot;
    public float yRot;
    public float boxScale;
    public float partialTicks;
    public int entityTickCount;
    public int effectTicksExisted;
    public int boxDeathTicks;
    public boolean invisibleToPlayer;
    public PBEffect pbEffect;
    public ItemStackRenderState renderItem = new ItemStackRenderState();
}
