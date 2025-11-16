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
import ivorius.pandorasbox.client.rendering.effects.renderstate.PandoraEffectRenderState;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
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
    public ResourceLocation texture = new ResourceLocation(PandorasBox.MOD_ID, "textures/entity/pandoras_box.png");

    public PandorasBoxRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);

        this.model = new PandorasBoxModel(renderManager.bakeLayer(PandorasBoxModel.LAYER_LOCATION));
        this.shadowRadius = 0.6F;
    }

    @Override
    public ResourceLocation getTextureLocation(PandorasBoxEntity entity) {
        return texture;
    }

    @Override
    public void render(PandorasBoxEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, poseStack, multiBufferSource, packedLightIn);
        poseStack.pushPose();
        poseStack.mulPose(YP.rotationDegrees(-entityYaw));
        entity.setXRot((float) (entity.getRatioBoxOpen(partialTicks) * 2F / 3F * Math.PI));

        PBEffectRenderer effectRenderer = PBEffectRenderingRegistry.rendererForID(entity.getBoxEffect().rendererResourceLocationForEffect());
        PandoraEffectRenderState pandoraEffectRenderState = effectRenderer.createRenderState();
        effectRenderer.extractRenderState(entity, pandoraEffectRenderState, entity.getBoxEffect(), entity.getEffectTicksExisted());
        float timePassed = calculateProgress(pandoraEffectRenderState, partialTicks);

        float boxScale = entity.getCurrentScale();
        if (boxScale < 1.0f)
            poseStack.scale(boxScale, boxScale, boxScale);
        float height = 0.0625F * Mth.sin((float) (timePassed * 4 * Math.PI));

        int packedOverlay = OverlayTexture.NO_OVERLAY;
        height += entity.getRenderItem().isEmpty() ? 1 : -0.25F;
        poseStack.translate(0, 0.5F + height, 0);
        if (entity.getRenderItem().isEmpty()) poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        else poseStack.mulPose(YP.rotation((float) (timePassed * 4 * Math.PI)));
        boolean visible = !entity.isInvisible();
        boolean visibleToPlayer = !visible && !entity.isInvisibleTo(Minecraft.getInstance().player);
        RenderType renderType = getRenderType(visible, visibleToPlayer);
        if (renderType != null) {
            if (entity.getRenderItem().isEmpty()) {
                this.model.setupAnim(entity, 0, 0, partialTicks, 0, 0);
                VertexConsumer buffer = ItemRenderer.getFoilBuffer(multiBufferSource, renderType, false, entity.hasFoil);
                this.model.renderToBuffer(poseStack, buffer, packedLightIn, packedOverlay, 0xFFFFFFFF);
            } else Minecraft.getInstance().getItemRenderer().renderStatic(entity.getRenderItem(), ItemDisplayContext.FIXED, packedLightIn, packedOverlay, poseStack, multiBufferSource, entity.level(), -1);
            if (pandoraEffectRenderState.shouldRender(entity.getDeathTicks())) {
                List<RenderLayer<PandorasBoxEntity, PandorasBoxModel>> layers = new ArrayList<>();
                effectRenderer.renderBox(this, entity, pandoraEffectRenderState, poseStack, multiBufferSource, packedLightIn, height, timePassed, partialTicks);
                List<RenderLayer<PandorasBoxEntity, PandorasBoxModel>> renderLayers = effectRenderer.getLayers(this, entity, pandoraEffectRenderState, model);
                if (renderLayers != null) {
                    layers.addAll(renderLayers);
                }

                for (RenderLayer<PandorasBoxEntity, PandorasBoxModel> renderLayer : layers) {
                    renderLayer.render(poseStack, multiBufferSource, packedLightIn, entity, 0, 0, partialTicks, partialTicks, 0, 0);
                }
            }
        }

        poseStack.popPose();
    }

    @Nullable
    protected RenderType getRenderType(boolean visible, boolean visibleToPlayer) {
        ResourceLocation resourceLocation = this.texture;
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

    public static float calculateProgress(PandoraEffectRenderState pandoraEffectRenderState, float partialTicks) {
        if (pandoraEffectRenderState.effectTicksExisted == -1) return 0;
        int animTicks = Math.max(pandoraEffectRenderState.maxTicksAlive, 20);
        return ((pandoraEffectRenderState.effectTicksExisted + partialTicks) % animTicks) / animTicks;
    }
}
