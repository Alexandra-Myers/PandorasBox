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
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.Objects;

import static com.mojang.math.Axis.YP;

/**
 * Created by lukas on 30.03.14.
 */
@OnlyIn(Dist.CLIENT)
public class PandorasBoxRenderer<T extends PandorasBoxEntity> extends EntityRenderer<T> {
    public PandorasBoxModel model;
    public ResourceLocation texture = new ResourceLocation(PandorasBox.MOD_ID, "textures/entity/pandoras_box.png");

//    public ResourceLocation model;

    public PandorasBoxRenderer(EntityRendererProvider.Context renderManager)
    {
        super(renderManager);

        model = new PandorasBoxModel(renderManager.bakeLayer(PandorasBoxModel.LAYER_LOCATION));
        shadowRadius = 0.6F;

//        model = new ResourceLocation(PandorasBox.MOD_ID, "block/pandoras_box.b3d");
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, matrixStack, renderTypeBuffer, packedLightIn);
        matrixStack.pushPose();
        model.PBE = entity;
//        matrixStack.translate(entity.getX(), entity.getY() + MathHelper.sin((entity.tickCount + partialTicks) * 0.04f) * 0.05, entity.getZ());
        matrixStack.mulPose(YP.rotationDegrees(-entityYaw));

        PBEffect effect = entity.getBoxEffect();
        boolean visible = !entity.isInvisible();
        VertexConsumer builder = renderTypeBuffer.getBuffer(Objects.requireNonNull(model.renderType(getTextureLocation(entity))));
        if (!effect.isDone(entity, entity.getEffectTicksExisted()) && entity.getDeathTicks() < 0)
        {
            PBEffectRenderer renderer = PBEffectRenderingRegistry.rendererForEffect(effect);
            if (renderer != null)
                renderer.renderBox(entity, effect, partialTicks, matrixStack, builder);
        }

        if (visible)
        {
            float boxScale = entity.getCurrentScale();
            if (boxScale < 1.0f)
                matrixStack.scale(boxScale, boxScale, boxScale);

            matrixStack.translate(0.0f, 1.5f, 0.0f);
            matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
            Arrow emptyEntity = new Arrow(entity.level(), entity.getX(), entity.getY(), entity.getZ());
            emptyEntity.setXRot(entity.getRatioBoxOpen(partialTicks) * 120.0f / 180.0f * 3.1415926f);
            int i = OverlayTexture.NO_OVERLAY;
            model.setupAnim(emptyEntity, 0, 0, partialTicks, 0, 0);
            model.renderToBuffer(matrixStack, builder, packedLightIn, i,1,1, 1, 1);
        }

        matrixStack.popPose();

    }
    @Override
    public ResourceLocation getTextureLocation(T var1)
    {
        return texture;
    }
}
