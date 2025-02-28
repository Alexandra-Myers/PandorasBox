package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderState;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.effects.PBEffectMulti;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Alexandra on 19.10.24.
 */
public class PBEffectRendererMulti implements PBEffectRenderer<PBEffectMulti> {
    @Override
    public void renderBox(PandorasBoxRenderer renderer, PandorasBoxRenderState renderState, PBEffectMulti effect, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, VertexConsumer consumer, int packedLightIn) {
        Arrays.stream(effect.effects).toList().forEach(pbEffect -> {
            PBEffectRenderer renderer1 = PBEffectRenderingRegistry.rendererForEffect(pbEffect);
            if (renderer1 != null && !pbEffect.isDone(renderState.effectTicksExisted)) {
                renderer1.renderBox(renderer, renderState, pbEffect, partialTicks, poseStack, multiBufferSource, consumer, packedLightIn);

            }
        });
    }

    @Override
    public List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> getLayers(PandorasBoxRenderer renderer, PandorasBoxRenderState renderState, PBEffectMulti effect, PandorasBoxModel model, float partialTicks) {
        List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> layers = new ArrayList<>();
        Arrays.stream(effect.effects).toList().forEach(pbEffect -> {
            PBEffectRenderer renderer1 = PBEffectRenderingRegistry.rendererForEffect(pbEffect);
            if (renderer1 != null && !pbEffect.isDone(renderState.effectTicksExisted)) {
                List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> renderLayers = renderer1.getLayers(renderer, renderState, pbEffect, model, partialTicks);
                if (renderLayers != null) {
                    layers.addAll(renderLayers);
                }
            }
        });
        return layers;
    }
}
