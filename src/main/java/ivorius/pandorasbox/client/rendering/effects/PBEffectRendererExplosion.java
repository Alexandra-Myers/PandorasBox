package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderState;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.client.rendering.effects.renderstate.ExplodeEffectRenderState;
import ivorius.pandorasbox.client.rendering.effects.renderstate.PandoraEffectRenderState;
import ivorius.pandorasbox.effects.PBEffectExplode;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import java.util.List;

/**
 * Created by lukas on 05.12.14.
 */
public class PBEffectRendererExplosion extends PBEffectRenderer<PBEffectExplode, ExplodeEffectRenderState> {
    @Override
    public void renderBox(PandorasBoxRenderer renderer, PandorasBoxRenderState renderState, ExplodeEffectRenderState effectRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLightIn, float height, float timePassed) {
        int lightColor = effectRenderState.burning ? 0xff0088 : 0xbb3399;

        float renderProgress = timePassed;
        renderProgress *= renderProgress;
        renderProgress *= renderProgress;

        float scale = (renderProgress * 0.3f) * effectRenderState.explosionRadius * 0.3f;
        IvRenderHelper.renderLights(effectRenderState.effectTicksExisted + renderState.partialTicks, scale, height, lightColor, renderProgress * 255F, 10, poseStack, submitNodeCollector);
    }

    @Override
    public PandoraEffectRenderState createRenderState() {
        return new ExplodeEffectRenderState();
    }

    @Override
    public List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> getLayers(PandorasBoxRenderer renderer, PandorasBoxRenderState renderState, ExplodeEffectRenderState effectRenderState, PandorasBoxModel model) {
        return null;
    }

    @Override
    public void extractRenderState(PandorasBoxRenderState boxRenderState, ExplodeEffectRenderState pandoraEffectRenderState, PBEffectExplode pandoraEffect, int effectTicksExisted) {
        super.extractRenderState(boxRenderState, pandoraEffectRenderState, pandoraEffect, effectTicksExisted);
        pandoraEffectRenderState.explosionRadius = pandoraEffect.explosionRadius;
        pandoraEffectRenderState.burning = pandoraEffect.burning;
    }
}
