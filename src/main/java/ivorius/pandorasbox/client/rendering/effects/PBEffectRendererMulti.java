package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderState;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.client.rendering.effects.renderstate.MultiEffectRenderState;
import ivorius.pandorasbox.client.rendering.effects.renderstate.PandoraEffectRenderState;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectMulti;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Alexandra on 19.10.24.
 */
public class PBEffectRendererMulti extends PBEffectRenderer<PBEffectMulti, MultiEffectRenderState> {
    @Override
    public void renderBox(PandorasBoxRenderer renderer, PandorasBoxRenderState renderState, MultiEffectRenderState effectRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLightIn, float height, float timePassed) {
        Arrays.stream(effectRenderState.effects).toList().forEach(pandoraEffectRenderState -> {
            PBEffectRenderer renderer1 = PBEffectRenderingRegistry.rendererForEffect(pandoraEffectRenderState);
            renderer1.renderBox(renderer, renderState, pandoraEffectRenderState, poseStack, submitNodeCollector, packedLightIn, height, ((pandoraEffectRenderState.effectTicksExisted + renderState.partialTicks) % pandoraEffectRenderState.maxTicksAlive) / pandoraEffectRenderState.maxTicksAlive);
        });
    }

    @Override
    public List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> getLayers(PandorasBoxRenderer renderer, PandorasBoxRenderState renderState, MultiEffectRenderState effectRenderState, PandorasBoxModel model) {
        List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> layers = new ArrayList<>();
        Arrays.stream(effectRenderState.effects).toList().forEach(pandoraEffectRenderState -> {
            PBEffectRenderer renderer1 = PBEffectRenderingRegistry.rendererForEffect(pandoraEffectRenderState);
            List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> renderLayers = renderer1.getLayers(renderer, renderState, pandoraEffectRenderState, model);
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
    public void extractRenderState(PandorasBoxRenderState boxRenderState, MultiEffectRenderState pandoraEffectRenderState, PBEffectMulti pandoraEffect, int effectTicksExisted) {
        super.extractRenderState(boxRenderState, pandoraEffectRenderState, pandoraEffect, effectTicksExisted);
        List<PandoraEffectRenderState> effectRenderStates = new ArrayList<>();
        for (PBEffect effect : pandoraEffect.getEffects()) {
            PBEffectRenderer renderer = PBEffectRenderingRegistry.rendererForID(effect.rendererIdentifierForEffect());
            int ticksExistedForEffect = pandoraEffect.getTicksExistedForEffect(effect, effectTicksExisted);
            if (effect.isDone(ticksExistedForEffect) && !renderer.rendersAfterDone()) continue;
            PandoraEffectRenderState renderState = renderer.createRenderState();
            renderer.extractRenderState(boxRenderState, renderState, effect, ticksExistedForEffect);
            effectRenderStates.add(renderState);
        }
        pandoraEffectRenderState.effects = effectRenderStates.toArray(PandoraEffectRenderState[]::new);
    }
}
