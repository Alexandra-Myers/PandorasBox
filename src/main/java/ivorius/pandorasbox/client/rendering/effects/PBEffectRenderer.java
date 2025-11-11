package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderState;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.client.rendering.effects.renderstate.PandoraEffectRenderState;
import ivorius.pandorasbox.effects.PBEffect;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import java.util.List;

/**
 * Created by lukas on 05.12.14.
 */
public class PBEffectRenderer<PE extends PBEffect, PERS extends PandoraEffectRenderState> {
    public void renderBox(PandorasBoxRenderer renderer, PandorasBoxRenderState renderState, PERS effectRenderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLightIn, float height, float timePassed) {

    }

    public List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> getLayers(PandorasBoxRenderer renderer, PandorasBoxRenderState renderState, PERS effectRenderState, PandorasBoxModel model) {
        return null;
    }

    public PandoraEffectRenderState createRenderState() {
        return new PandoraEffectRenderState();
    }

    public void extractRenderState(PandorasBoxRenderState boxRenderState, PERS pandoraEffectRenderState, PE pandoraEffect, int effectTicksExisted) {
        pandoraEffectRenderState.renderer = pandoraEffect.rendererResourceLocationForEffect();
        pandoraEffectRenderState.maxTicksAlive = pandoraEffect.getMaxTicksAlive();
        pandoraEffectRenderState.isDone = pandoraEffect.isDone(effectTicksExisted);
        pandoraEffectRenderState.effectTicksExisted = effectTicksExisted;
        pandoraEffectRenderState.rendersAnyways = rendersAfterDone();
    }

    public boolean rendersAfterDone() {
        return false;
    }
}
