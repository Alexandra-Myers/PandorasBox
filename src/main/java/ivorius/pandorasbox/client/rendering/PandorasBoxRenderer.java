/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRenderer;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRenderingRegistry;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.mojang.math.Axis.YP;

/**
 * Created by lukas on 30.03.14.
 */
@Environment(EnvType.CLIENT)
public class PandorasBoxRenderer extends EntityRenderer<PandorasBoxEntity> implements RenderLayerParent<PandorasBoxEntity, PandorasBoxModel> {
    public PandorasBoxModel model;
    public ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "textures/entity/pandoras_box.png");

    public PandorasBoxRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);

        model = new PandorasBoxModel(renderManager.bakeLayer(PandorasBoxModel.LAYER_LOCATION));
        shadowRadius = 0.6F;
    }

    @Override
    public void render(@NotNull PandorasBoxEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, poseStack, multiBufferSource, packedLightIn);
        poseStack.pushPose();
        poseStack.mulPose(YP.rotationDegrees(-entityYaw));

        PBEffect effect = entity.getBoxEffect();

        float boxScale = entity.getCurrentScale();
        if (boxScale < 1.0f)
            poseStack.scale(boxScale, boxScale, boxScale);

        poseStack.translate(0.0f, 1.5f, 0.0f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        entity.setXRot(entity.getRatioBoxOpen(partialTicks) * 120.0f / 180.0f * 3.1415926f);
        int packedOverlay = OverlayTexture.NO_OVERLAY;
        model.setupAnim(entity, 0, 0, partialTicks, 0, 0);
        boolean visible = !entity.isInvisible();
        boolean visibleToPlayer = !visible && !entity.isInvisibleTo(Minecraft.getInstance().player);
        RenderType renderType = getRenderType(entity, visible, visibleToPlayer);
        if (renderType != null) {
            VertexConsumer consumer = multiBufferSource.getBuffer(renderType);
            model.renderToBuffer(poseStack, consumer, packedLightIn, packedOverlay, 0xFFFFFFFF);
            if (!effect.isDone(entity, entity.getEffectTicksExisted()) && entity.getDeathTicks() < 0) {
                List<RenderLayer<PandorasBoxEntity, PandorasBoxModel>> layers = new ArrayList<>();
                PBEffectRenderer renderer = PBEffectRenderingRegistry.rendererForEffect(effect);
                if (renderer != null) {
                    renderer.renderBox(this, entity, effect, partialTicks, poseStack, multiBufferSource, consumer, packedLightIn);
                    List<RenderLayer<PandorasBoxEntity, PandorasBoxModel>> renderLayers = renderer.getLayers(this, entity, effect, model, partialTicks);
                    if (renderLayers != null) {
                        layers.addAll(renderLayers);
                    }
                }

                for (RenderLayer<PandorasBoxEntity, PandorasBoxModel> renderLayer : layers) {
                    renderLayer.render(poseStack, multiBufferSource, packedLightIn, entity, 0, 0, partialTicks, partialTicks, 0, 0);
                }
            }
        }

        poseStack.popPose();

    }
    @Nullable
    protected RenderType getRenderType(PandorasBoxEntity pandorasBox, boolean visible, boolean visibleToPlayer) {
        ResourceLocation resourceLocation = this.getTextureLocation(pandorasBox);
        if (visibleToPlayer) {
            return RenderType.itemEntityTranslucentCull(resourceLocation);
        } else if (visible) {
            return this.model.renderType(resourceLocation);
        }
        return null;
    }

    @Override
    public PandorasBoxModel getModel() {
        return model;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull PandorasBoxEntity var1) {
        return texture;
    }
}
