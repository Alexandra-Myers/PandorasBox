/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.client.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRenderer;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRenderingRegistry;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectMulti;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by lukas on 30.03.14.
 */
@OnlyIn(Dist.CLIENT)
public class PandorasBoxRenderer<T extends PandorasBoxEntity> extends EntityRenderer<T> {
    public PandorasBoxModel model;
    public ResourceLocation texture = new ResourceLocation(PandorasBox.MOD_ID, "textures/entity/pandoras_box.png");

    public PandorasBoxRenderer(EntityRendererManager renderManager) {
        super(renderManager);

        model = new PandorasBoxModel();
        shadowRadius = 0.6F;
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, matrixStack, renderTypeBuffer, packedLightIn);
        matrixStack.pushPose();
        model.PBE = entity;
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(-entityYaw));

        PBEffect effect = entity.getBoxEffect();
        boolean visible = !entity.isInvisible();
        IVertexBuilder builder = renderTypeBuffer.getBuffer(Objects.requireNonNull(model.renderType(getTextureLocation(entity))));

        if (visible) {
            float boxScale = entity.getCurrentScale();
            if (boxScale < 1.0f)
                matrixStack.scale(boxScale, boxScale, boxScale);

            matrixStack.translate(0.0f, 1.5f, 0.0f);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0f));
            ArrowEntity emptyEntity = new ArrowEntity(entity.level, entity.getX(), entity.getY(), entity.getZ());
            emptyEntity.xRot = entity.getRatioBoxOpen(partialTicks) * 120.0f / 180.0f * 3.1415926f;
            int i = OverlayTexture.NO_OVERLAY;
            model.setupAnim(emptyEntity, 0, 0, partialTicks, 0, 0);
            model.renderToBuffer(matrixStack, builder, packedLightIn, i,1,1, 1, 1);
            if (!effect.isDone(entity, entity.getEffectTicksExisted()) && entity.getDeathTicks() < 0) {
                if (effect instanceof PBEffectMulti) {
                    PBEffectMulti pbEffectMulti = (PBEffectMulti) effect;
                    Arrays.stream(pbEffectMulti.effects).forEach(pbEffect -> {
                        PBEffectRenderer renderer = PBEffectRenderingRegistry.rendererForEffect(pbEffect);
                        if (renderer != null && !pbEffect.isDone(entity, entity.getEffectTicksExisted()))
                            renderer.renderBox(entity, pbEffect, partialTicks, matrixStack, renderTypeBuffer, builder);
                    });
                } else {
                    PBEffectRenderer renderer = PBEffectRenderingRegistry.rendererForEffect(effect);
                    if (renderer != null)
                        renderer.renderBox(entity, effect, partialTicks, matrixStack, renderTypeBuffer, builder);
                }
            }
        }

        matrixStack.popPose();

    }
    @Override
    public ResourceLocation getTextureLocation(T var1)
    {
        return texture;
    }
}
