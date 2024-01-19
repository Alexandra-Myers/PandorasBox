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
import ivorius.pandorasbox.effects.PBEffectMulti;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Arrow;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

import static com.mojang.math.Axis.YP;

/**
 * Created by lukas on 30.03.14.
 */
@OnlyIn(Dist.CLIENT)
public class PandorasBoxRenderer<T extends PandorasBoxEntity> extends EntityRenderer<T> {
    public PandorasBoxModel model;
    public ResourceLocation texture = new ResourceLocation(PandorasBox.MOD_ID, "textures/entity/pandoras_box.png");

    public PandorasBoxRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager);

        model = new PandorasBoxModel(renderManager.bakeLayer(PandorasBoxModel.LAYER_LOCATION));
        shadowRadius = 0.6F;
    }

    @Override
    public void render(@NotNull T entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, poseStack, multiBufferSource, packedLightIn);
        poseStack.pushPose();
        model.PBE = entity;
        poseStack.mulPose(YP.rotationDegrees(-entityYaw));

        PBEffect effect = entity.getBoxEffect();
        boolean visible = !entity.isInvisible();
        VertexConsumer consumer = multiBufferSource.getBuffer(Objects.requireNonNull(model.renderType(getTextureLocation(entity))));

        if (visible) {
            float boxScale = entity.getCurrentScale();
            if (boxScale < 1.0f)
                poseStack.scale(boxScale, boxScale, boxScale);

            poseStack.translate(0.0f, 1.5f, 0.0f);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
            Arrow emptyEntity = new Arrow(entity.level(), entity.getX(), entity.getY(), entity.getZ());
            emptyEntity.setXRot(entity.getRatioBoxOpen(partialTicks) * 120.0f / 180.0f * 3.1415926f);
            int i = OverlayTexture.NO_OVERLAY;
            model.setupAnim(emptyEntity, 0, 0, partialTicks, 0, 0);
            model.renderToBuffer(poseStack, consumer, packedLightIn, i,1,1, 1, 1);
            if (!effect.isDone(entity, entity.getEffectTicksExisted()) && entity.getDeathTicks() < 0) {
                if(effect instanceof PBEffectMulti pbEffectMulti)
                    Arrays.stream(pbEffectMulti.effects).toList().forEach(pbEffect -> {
                        PBEffectRenderer renderer = PBEffectRenderingRegistry.rendererForEffect(pbEffect);
                        if (renderer != null && !pbEffect.isDone(entity, entity.getEffectTicksExisted()))
                            renderer.renderBox(entity, pbEffect, partialTicks, poseStack, multiBufferSource, consumer);
                    });
                else {
                    PBEffectRenderer renderer = PBEffectRenderingRegistry.rendererForEffect(effect);
                    if (renderer != null)
                        renderer.renderBox(entity, effect, partialTicks, poseStack, multiBufferSource, consumer);
                }
            }
        }

        poseStack.popPose();

    }
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T var1)
    {
        return texture;
    }
}
