package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderState;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.client.rendering.effects.renderstate.MeltdownEffectRenderState;
import ivorius.pandorasbox.client.rendering.effects.renderstate.PandoraEffectRenderState;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectMeltdown;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Alexandra on 18.10.24.
 */
public class PBEffectRendererMeltdown extends PBEffectRenderer<PBEffectMeltdown, MeltdownEffectRenderState> {
    public Identifier meltdownTexture1 = Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "textures/entity/pandoras_box_unstable_1.png");
    public Identifier meltdownTexture2 = Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "textures/entity/pandoras_box_unstable_2.png");
    public Identifier meltdownTexture3 = Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, "textures/entity/pandoras_box_unstable_3.png");

    @Override
    public void renderBox(PandorasBoxRenderer renderer, PandorasBoxRenderState renderState, MeltdownEffectRenderState effectRenderState, float partialTicks, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLightIn, float height) {
        int lightColor = 0xff6611;

        float timePassed = Math.min((float) renderState.effectTicksExisted / effectRenderState.maxTicksAlive, 1F);
        if (timePassed >= 0.8) {
            timePassed *= timePassed;
            timePassed *= timePassed;
            timePassed *= timePassed * 0.5F;

            float scale = (timePassed * 0.3f) * effectRenderState.range * 0.3f;
            IvRenderHelper.renderLights(renderState.entityTickCount + partialTicks, scale, height, lightColor, timePassed * 255F, 10, poseStack, submitNodeCollector);
        }
        Arrays.stream(effectRenderState.effects).toList().forEach(pandoraEffectRenderState -> {
            PBEffectRenderer renderer1 = PBEffectRenderingRegistry.rendererForEffect(pandoraEffectRenderState);
            if (renderer1 != null && !pandoraEffectRenderState.isDone)
                renderer1.renderBox(renderer, renderState, pandoraEffectRenderState, partialTicks, poseStack, submitNodeCollector, packedLightIn, height);
        });
        if (!renderState.renderItem.isEmpty()) return;
        timePassed = Math.min((float) renderState.effectTicksExisted / effectRenderState.maxTicksAlive, 1F);
        submitNodeCollector.submitModel(renderer.model, renderState, poseStack, RenderTypes.entityTranslucent(getTextureForProgress(timePassed)), packedLightIn, OverlayTexture.NO_OVERLAY, renderState.outlineColor, null);
    }

    @Override
    public List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> getLayers(PandorasBoxRenderer renderer, PandorasBoxRenderState renderState, MeltdownEffectRenderState effectRenderState, PandorasBoxModel model, float partialTicks) {
        List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> layers = new ArrayList<>();
        Arrays.stream(effectRenderState.effects).toList().forEach(pandoraEffectRenderState -> {
            PBEffectRenderer renderer1 = PBEffectRenderingRegistry.rendererForEffect(pandoraEffectRenderState);
            if (renderer1 != null && !pandoraEffectRenderState.isDone) {
                List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> renderLayers = renderer1.getLayers(renderer, renderState, pandoraEffectRenderState, model, partialTicks);
                if (renderLayers != null) {
                    layers.addAll(renderLayers);
                }
            }
        });
        return layers;
    }

    @Override
    public PandoraEffectRenderState createRenderState() {
        return new MeltdownEffectRenderState();
    }

    public Identifier getTextureForProgress(float progress) {
        return progress >= 0.75 ? meltdownTexture3 : progress >= 0.35 ? meltdownTexture2 : meltdownTexture1;
    }

    @Override
    public void extractRenderState(PandorasBoxRenderState boxRenderState, MeltdownEffectRenderState pandoraEffectRenderState, PBEffectMeltdown pandoraEffect, int effectTicksExisted) {
        super.extractRenderState(boxRenderState, pandoraEffectRenderState, pandoraEffect, effectTicksExisted);
        pandoraEffectRenderState.range = pandoraEffect.getRange();
        List<PandoraEffectRenderState> effectRenderStates = new ArrayList<>();
        for (PBEffect effect : pandoraEffect.getEffects()) {
            PBEffectRenderer renderer = PBEffectRenderingRegistry.rendererForID(effect.rendererIdentifierForEffect());
            PandoraEffectRenderState renderState = renderer.createRenderState();
            renderer.extractRenderState(boxRenderState, renderState, effect, pandoraEffect.getTicksExistedForEffect(effect, effectTicksExisted));
            effectRenderStates.add(renderState);
        }
        pandoraEffectRenderState.effects = effectRenderStates.toArray(PandoraEffectRenderState[]::new);
    }
}
