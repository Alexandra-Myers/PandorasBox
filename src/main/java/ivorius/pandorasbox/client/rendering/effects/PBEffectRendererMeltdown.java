package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.effects.PBEffectMeltdown;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
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

    @Override
    public void renderBox(PandorasBoxRenderer renderer, PandorasBoxEntity entity, PBEffectMeltdown effect, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, VertexConsumer consumer, int packedLightIn) {
        int lightColor = 0xff6611;

        float timePassed = Math.min((float) entity.getEffectTicksExisted() / effect.maxTicksAlive, 1F);
        if (timePassed >= 0.75) {
            timePassed *= timePassed;
            timePassed *= timePassed;
            timePassed *= timePassed * 0.5F;

            float scale = (timePassed * 0.3f) * effect.range * 0.3f;
            IvRenderHelper.renderLights(entity.tickCount + partialTicks, scale, lightColor, timePassed * 255F, 10, poseStack, multiBufferSource);
        }
        Arrays.stream(effect.effects).toList().forEach(pbEffect -> {
            PBEffectRenderer renderer1 = PBEffectRenderingRegistry.rendererForEffect(pbEffect);
            if (renderer1 != null && !pbEffect.isDone(entity, entity.getEffectTicksExisted()))
                renderer1.renderBox(renderer, entity, pbEffect, partialTicks, poseStack, multiBufferSource, consumer, packedLightIn);
        });
        timePassed = Math.min((float) entity.getEffectTicksExisted() / effect.maxTicksAlive, 1F);
        VertexConsumer newConsumer = multiBufferSource.getBuffer(RenderType.entityTranslucentCull(getTextureForProgress(timePassed)));
        renderer.model.renderToBuffer(poseStack, newConsumer, packedLightIn, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
    }

    @Override
    public List<RenderLayer<PandorasBoxEntity, PandorasBoxModel>> getLayers(PandorasBoxRenderer renderer, PandorasBoxEntity entity, PBEffectMeltdown effect, PandorasBoxModel model, float partialTicks) {
        List<RenderLayer<PandorasBoxEntity, PandorasBoxModel>> layers = new ArrayList<>();
        Arrays.stream(effect.effects).toList().forEach(pbEffect -> {
            PBEffectRenderer renderer1 = PBEffectRenderingRegistry.rendererForEffect(pbEffect);
            if (renderer1 != null && !pbEffect.isDone(entity, entity.getEffectTicksExisted())) {
                List<RenderLayer<PandorasBoxEntity, PandorasBoxModel>> renderLayers = renderer1.getLayers(renderer, entity, pbEffect, model, partialTicks);
                if (renderLayers != null) {
                    layers.addAll(renderLayers);
                }
            }
        });
        return layers;
    }

    public ResourceLocation getTextureForProgress(float progress) {
        return progress >= 0.675 ? meltdownTexture2 : meltdownTexture1;
    }
}
