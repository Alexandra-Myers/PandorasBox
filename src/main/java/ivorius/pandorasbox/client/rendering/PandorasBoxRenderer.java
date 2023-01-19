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
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Created by lukas on 30.03.14.
 */
@OnlyIn(Dist.CLIENT)
public class PandorasBoxRenderer<T extends PandorasBoxEntity> extends EntityRenderer<T> {
    public PandorasBoxModel model;
    public ResourceLocation texture = new ResourceLocation(PandorasBox.MOD_ID, "textures/entity/pandoras_box.png");

//    public ResourceLocation model;

    public PandorasBoxRenderer(EntityRendererManager renderManager)
    {
        super(renderManager);

        model = new PandorasBoxModel();
        shadowRadius = 0.6F;

//        model = new ResourceLocation(PandorasBox.MOD_ID, "block/pandoras_box.b3d");
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, matrixStack, renderTypeBuffer, packedLightIn);
        matrixStack.pushPose();
        matrixStack.translate(entity.getX(), entity.getY() + MathHelper.sin((entity.tickCount + partialTicks) * 0.04f) * 0.05, entity.getZ());
        matrixStack.mulPose(new Quaternion(0.0F, 1.0F, 0.0F, -entityYaw));

        PBEffect effect = entity.getBoxEffect();
        boolean visible = !entity.isInvisible();
        IVertexBuilder builder = renderTypeBuffer.getBuffer(Objects.requireNonNull(model.renderType(getTextureLocation(entity))));
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
            matrixStack.mulPose(new Quaternion(0.0f, 0.0f, 1.0f, 180.0f));
            /*ArrowEntity emptyEntity = new ArrowEntity(entity.level, entity.getX(), entity.getY(), entity.getZ());
            emptyEntity.xRot = entity.getRatioBoxOpen(partialTicks) * 120.0f / 180.0f * 3.1415926f;*/
            int i = OverlayTexture.NO_OVERLAY;
            model.setupAnim(entity, 0, 0, 0, 0, 0);
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
