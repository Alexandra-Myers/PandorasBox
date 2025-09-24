package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderState;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
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
public class PBEffectRendererMeltdown implements PBEffectRenderer<PBEffectMeltdown> {
    public ResourceLocation meltdownTexture1 = ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "textures/entity/pandoras_box_unstable_1.png");
    public ResourceLocation meltdownTexture2 = ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "textures/entity/pandoras_box_unstable_2.png");
    public ResourceLocation meltdownTexture3 = ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "textures/entity/pandoras_box_unstable_3.png");

    @Override
    public void renderBox(PandorasBoxRenderer renderer, PandorasBoxRenderState renderState, PBEffectMeltdown effect, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, VertexConsumer consumer, int packedLightIn, float height) {
        int lightColor = 0xff6611;

        float timePassed = Math.min((float) renderState.effectTicksExisted / effect.getMaxTicksAlive(), 1F);
        if (timePassed >= 0.8) {
            timePassed *= timePassed;
            timePassed *= timePassed;
            timePassed *= timePassed * 0.5F;

            float scale = (timePassed * 0.3f) * effect.getRange() * 0.3f;
            IvRenderHelper.renderLights(renderState.entityTickCount + partialTicks, scale, height, lightColor, timePassed * 255F, 10, poseStack, multiBufferSource);
        }
        Arrays.stream(effect.getEffects()).toList().forEach(pbEffect -> {
            PBEffectRenderer renderer1 = PBEffectRenderingRegistry.rendererForEffect(pbEffect);
            if (renderer1 != null && !pbEffect.isDone(renderState.effectTicksExisted))
                renderer1.renderBox(renderer, renderState, pbEffect, partialTicks, poseStack, multiBufferSource, consumer, packedLightIn, height);
        });
        if (!renderState.renderItem.isEmpty()) return;
        timePassed = Math.min((float) renderState.effectTicksExisted / effect.getMaxTicksAlive(), 1F);
        VertexConsumer newConsumer = multiBufferSource.getBuffer(RenderType.entityTranslucent(getTextureForProgress(timePassed)));
        renderer.model.renderToBuffer(poseStack, newConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
    }

    @Override
    public List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> getLayers(PandorasBoxRenderer renderer, PandorasBoxRenderState renderState, PBEffectMeltdown effect, PandorasBoxModel model, float partialTicks) {
        List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> layers = new ArrayList<>();
        Arrays.stream(effect.getEffects()).toList().forEach(pbEffect -> {
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

    public ResourceLocation getTextureForProgress(float progress) {
        return progress >= 0.75 ? meltdownTexture3 : progress >= 0.35 ? meltdownTexture2 : meltdownTexture1;
    }
}
