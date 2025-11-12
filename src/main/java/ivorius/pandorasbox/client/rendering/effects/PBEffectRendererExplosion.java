package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.client.rendering.effects.renderstate.ExplodeEffectRenderState;
import ivorius.pandorasbox.client.rendering.effects.renderstate.PandoraEffectRenderState;
import ivorius.pandorasbox.effects.PBEffectExplode;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import java.util.List;

/**
 * Created by lukas on 05.12.14.
 */
public class PBEffectRendererExplosion extends PBEffectRenderer<PBEffectExplode, ExplodeEffectRenderState> {
    @Override
    public void renderBox(PandorasBoxRenderer renderer, PandorasBoxEntity pandorasBoxEntity, ExplodeEffectRenderState effectRenderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLightIn, float height, float timePassed, float partialTicks) {
        int lightColor = effectRenderState.burning ? 0xff0088 : 0xbb3399;

        float renderProgress = timePassed;
        renderProgress *= renderProgress;
        renderProgress *= renderProgress;

        float scale = (renderProgress * 0.3f) * effectRenderState.explosionRadius * 0.3f;
        IvRenderHelper.renderLights(effectRenderState.effectTicksExisted + partialTicks, scale, height, lightColor, renderProgress * 255F, 10, poseStack, multiBufferSource);
    }

    @Override
    public PandoraEffectRenderState createRenderState() {
        return new ExplodeEffectRenderState();
    }

    @Override
    public List<RenderLayer<PandorasBoxEntity, PandorasBoxModel>> getLayers(PandorasBoxRenderer renderer, PandorasBoxEntity pandorasBoxEntity, ExplodeEffectRenderState effectRenderState, PandorasBoxModel model) {
        return null;
    }

    @Override
    public void extractRenderState(PandorasBoxEntity entity, ExplodeEffectRenderState pandoraEffectRenderState, PBEffectExplode pandoraEffect, int effectTicksExisted) {
        super.extractRenderState(entity, pandoraEffectRenderState, pandoraEffect, effectTicksExisted);
        pandoraEffectRenderState.explosionRadius = pandoraEffect.explosionRadius;
        pandoraEffectRenderState.burning = pandoraEffect.burning;
    }
}
