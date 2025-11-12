package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.client.rendering.effects.renderstate.MultiEffectRenderState;
import ivorius.pandorasbox.client.rendering.effects.renderstate.PandoraEffectRenderState;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectMulti;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Alexandra on 19.10.24.
 */
public class PBEffectRendererMulti extends PBEffectRenderer<PBEffectMulti, MultiEffectRenderState> {
    @Override
    public void renderBox(PandorasBoxRenderer renderer, PandorasBoxEntity pandorasBoxEntity, MultiEffectRenderState effectRenderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLightIn, float height, float timePassed, float partialTicks) {
        Arrays.stream(effectRenderState.effects).toList().forEach(pandoraEffectRenderState -> {
            PBEffectRenderer renderer1 = PBEffectRenderingRegistry.rendererForEffect(pandoraEffectRenderState);
            renderer1.renderBox(renderer, pandorasBoxEntity, pandoraEffectRenderState, poseStack, multiBufferSource, packedLightIn, height, PandorasBoxRenderer.calculateProgress(pandoraEffectRenderState, partialTicks), partialTicks);
        });
    }

    @Override
    public List<RenderLayer<PandorasBoxEntity, PandorasBoxModel>> getLayers(PandorasBoxRenderer renderer, PandorasBoxEntity pandorasBoxEntity, MultiEffectRenderState effectRenderState, PandorasBoxModel model) {
        List<RenderLayer<PandorasBoxEntity, PandorasBoxModel>> layers = new ArrayList<>();
        Arrays.stream(effectRenderState.effects).toList().forEach(pandoraEffectRenderState -> {
            PBEffectRenderer renderer1 = PBEffectRenderingRegistry.rendererForEffect(pandoraEffectRenderState);
            List<RenderLayer<PandorasBoxEntity, PandorasBoxModel>> renderLayers = renderer1.getLayers(renderer, pandorasBoxEntity, pandoraEffectRenderState, model);
            if (renderLayers != null) {
                layers.addAll(renderLayers);
            }
        });
        return layers;
    }

    @Override
    public PandoraEffectRenderState createRenderState() {
        return new MultiEffectRenderState();
    }

    @Override
    public void extractRenderState(PandorasBoxEntity entity, MultiEffectRenderState pandoraEffectRenderState, PBEffectMulti pandoraEffect, int effectTicksExisted) {
        super.extractRenderState(entity, pandoraEffectRenderState, pandoraEffect, effectTicksExisted);
        List<PandoraEffectRenderState> effectRenderStates = new ArrayList<>();
        for (PBEffect effect : pandoraEffect.getEffects()) {
            PBEffectRenderer renderer = PBEffectRenderingRegistry.rendererForID(effect.rendererResourceLocationForEffect());
            int ticksExistedForEffect = pandoraEffect.getTicksExistedForEffect(effect, effectTicksExisted);
            if (effect.isDone(ticksExistedForEffect) && !renderer.rendersAfterDone()) continue;
            PandoraEffectRenderState renderState = renderer.createRenderState();
            renderer.extractRenderState(entity, renderState, effect, ticksExistedForEffect);
            effectRenderStates.add(renderState);
        }
        pandoraEffectRenderState.effects = effectRenderStates.toArray(PandoraEffectRenderState[]::new);
    }
}
