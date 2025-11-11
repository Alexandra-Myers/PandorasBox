package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderState;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.client.rendering.effects.renderstate.MeltdownEffectRenderState;
import ivorius.pandorasbox.client.rendering.effects.renderstate.PandoraEffectRenderState;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectMeltdown;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Alexandra on 18.10.24.
 */
public class PBEffectRendererMeltdown extends PBEffectRenderer<PBEffectMeltdown, MeltdownEffectRenderState> {
    public ResourceLocation meltdownTexture1 = ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "textures/entity/pandoras_box_unstable_1.png");
    public ResourceLocation meltdownTexture2 = ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "textures/entity/pandoras_box_unstable_2.png");
    public ResourceLocation meltdownTexture3 = ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "textures/entity/pandoras_box_unstable_3.png");

    @Override
    public void renderBox(PandorasBoxRenderer renderer, PandorasBoxRenderState renderState, MeltdownEffectRenderState effectRenderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLightIn, float height, float timePassed) {
        int lightColor = 0xff6611;

        if (timePassed >= 0.8) {
            float renderProgress = timePassed;
            renderProgress *= renderProgress;
            renderProgress *= renderProgress;
            renderProgress *= renderProgress * 0.5F;

            float scale = (renderProgress * 0.3f) * effectRenderState.range * 0.3f;
            IvRenderHelper.renderLights(effectRenderState.effectTicksExisted + renderState.partialTicks, scale, height, lightColor, renderProgress * 255F, 10, poseStack, multiBufferSource);
        }
        Arrays.stream(effectRenderState.effects).toList().forEach(pandoraEffectRenderState -> {
            PBEffectRenderer renderer1 = PBEffectRenderingRegistry.rendererForEffect(pandoraEffectRenderState);
            renderer1.renderBox(renderer, renderState, pandoraEffectRenderState, poseStack, multiBufferSource, packedLightIn, height, PandorasBoxRenderer.calculateProgress(renderState, pandoraEffectRenderState));
        });
        if (!renderState.renderItem.isEmpty()) return;
        VertexConsumer newConsumer = multiBufferSource.getBuffer(RenderType.entityTranslucent(getTextureForProgress(timePassed)));
        renderer.model.renderToBuffer(poseStack, newConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
    }

    @Override
    public List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> getLayers(PandorasBoxRenderer renderer, PandorasBoxRenderState renderState, MeltdownEffectRenderState effectRenderState, PandorasBoxModel model) {
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
        return new MeltdownEffectRenderState();
    }

    public ResourceLocation getTextureForProgress(float progress) {
        return progress >= 0.75 ? meltdownTexture3 : progress >= 0.35 ? meltdownTexture2 : meltdownTexture1;
    }

    @Override
    public void extractRenderState(PandorasBoxRenderState boxRenderState, MeltdownEffectRenderState pandoraEffectRenderState, PBEffectMeltdown pandoraEffect, int effectTicksExisted) {
        super.extractRenderState(boxRenderState, pandoraEffectRenderState, pandoraEffect, effectTicksExisted);
        pandoraEffectRenderState.range = pandoraEffect.getRange();
        List<PandoraEffectRenderState> effectRenderStates = new ArrayList<>();
        for (PBEffect effect : pandoraEffect.getEffects()) {
            PBEffectRenderer renderer = PBEffectRenderingRegistry.rendererForID(effect.rendererResourceLocationForEffect());
            int ticksExistedForEffect = pandoraEffect.getTicksExistedForEffect(effect, effectTicksExisted);
            if (effect.isDone(ticksExistedForEffect) && !renderer.rendersAfterDone()) continue;
            PandoraEffectRenderState renderState = renderer.createRenderState();
            renderer.extractRenderState(boxRenderState, renderState, effect, ticksExistedForEffect);
            effectRenderStates.add(renderState);
        }
        pandoraEffectRenderState.effects = effectRenderStates.toArray(PandoraEffectRenderState[]::new);
    }

    @Override
    public boolean rendersAfterDone() {
        return true;
    }
}
