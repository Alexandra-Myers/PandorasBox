package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderState;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.effects.PBEffectExplode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import java.util.List;

/**
 * Created by lukas on 05.12.14.
 */
public class PBEffectRendererExplosion implements PBEffectRenderer<PBEffectExplode> {
    @Override
    public void renderBox(PandorasBoxRenderer renderer, PandorasBoxRenderState renderState, PBEffectExplode effect, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, VertexConsumer consumer, int packedLightIn, float height) {
        int lightColor = effect.burning ? 0xff0088 : 0xbb3399;

        float timePassed = Math.min((float) renderState.effectTicksExisted / (float) effect.maxTicksAlive, 1F);
        timePassed *= timePassed;
        timePassed *= timePassed;

        float scale = (timePassed * 0.3f) * effect.explosionRadius * 0.3f;
        IvRenderHelper.renderLights(renderState.entityTickCount + partialTicks, scale, height, lightColor, timePassed * 255F, 10, poseStack, multiBufferSource);
    }

    @Override
    public List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> getLayers(PandorasBoxRenderer renderer, PandorasBoxRenderState renderState, PBEffectExplode effect, PandorasBoxModel model, float partialTicks) {
        return null;
    }
}
