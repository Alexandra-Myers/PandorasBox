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
public class PandorasBoxRenderer extends EntityRenderer<PandorasBoxEntity, PandorasBoxRenderState> implements RenderLayerParent<PandorasBoxRenderState, PandorasBoxModel> {
    public PandorasBoxModel model;
    public ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "textures/entity/pandoras_box.png");

    public PandorasBoxRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);

        model = new PandorasBoxModel(renderManager.bakeLayer(PandorasBoxModel.LAYER_LOCATION));
        shadowRadius = 0.6F;
    }

    @Override
    public @NotNull PandorasBoxRenderState createRenderState() {
        return new PandorasBoxRenderState();
    }

    @Override
    public void render(PandorasBoxRenderState renderState, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLightIn) {
        super.render(renderState, poseStack, multiBufferSource, packedLightIn);
        poseStack.pushPose();
        poseStack.mulPose(YP.rotationDegrees(-renderState.yRot));

        PBEffect effect = renderState.pbEffect;

        float boxScale = renderState.boxScale;
        if (boxScale < 1.0f)
            poseStack.scale(boxScale, boxScale, boxScale);

        poseStack.translate(0.0f, 1.5f, 0.0f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        int packedOverlay = OverlayTexture.NO_OVERLAY;
        model.setupAnim(renderState);
        boolean visible = !renderState.isInvisible;
        boolean visibleToPlayer = !visible && !renderState.invisibleToPlayer;
        RenderType renderType = getRenderType(visible, visibleToPlayer);
        if (renderType != null) {
            VertexConsumer consumer = multiBufferSource.getBuffer(renderType);
            model.renderToBuffer(poseStack, consumer, packedLightIn, packedOverlay, 0xFFFFFFFF);
            if (!effect.isDone(renderState.effectTicksExisted) && renderState.boxDeathTicks < 0) {
                List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> layers = new ArrayList<>();
                PBEffectRenderer renderer = PBEffectRenderingRegistry.rendererForEffect(effect);
                if (renderer != null) {
                    renderer.renderBox(this, renderState, effect, renderState.partialTicks, poseStack, multiBufferSource, consumer, packedLightIn);
                    List<RenderLayer<PandorasBoxRenderState, PandorasBoxModel>> renderLayers = renderer.getLayers(this, renderState, effect, model, renderState.partialTicks);
                    if (renderLayers != null) {
                        layers.addAll(renderLayers);
                    }
                }

                for (RenderLayer<PandorasBoxRenderState, PandorasBoxModel> renderLayer : layers) {
                    renderLayer.render(poseStack, multiBufferSource, packedLightIn, renderState, 0, 0);
                }
            }
        }

        poseStack.popPose();
    }
    @Nullable
    protected RenderType getRenderType(boolean visible, boolean visibleToPlayer) {
        ResourceLocation resourceLocation = texture;
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
    public void extractRenderState(PandorasBoxEntity entity, PandorasBoxRenderState entityRenderState, float partialTicks) {
        super.extractRenderState(entity, entityRenderState, partialTicks);
        entityRenderState.xRot = entity.getRatioBoxOpen(partialTicks) * 120.0f / 180.0f * 3.1415926f;
        entityRenderState.yRot = entity.getYRot();
        entityRenderState.boxScale = entity.getCurrentScale();
        entityRenderState.partialTicks = partialTicks;
        entityRenderState.effectTicksExisted = entity.getEffectTicksExisted();
        entityRenderState.entityTickCount = entity.tickCount;
        entityRenderState.boxDeathTicks = entity.getDeathTicks();
        entityRenderState.pbEffect = entity.getBoxEffect();
        entityRenderState.invisibleToPlayer = entity.isInvisibleTo(Minecraft.getInstance().player);
    }
}
