/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.client.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRenderer;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRenderingRegistry;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.entitites.EntityPandorasBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
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
public class RenderPandorasBox<T extends EntityPandorasBox> extends EntityRenderer<T> implements IRenderFactory<T> {
    public ModelPandorasBox model;
    public ResourceLocation texture = new ResourceLocation(PandorasBox.MOD_ID, "textures/entity/pandoras_box.png");

//    public ResourceLocation model;

    public RenderPandorasBox(EntityRendererManager renderManager)
    {
        super(renderManager);

        model = new ModelPandorasBox();
        shadowRadius = 0.4F;

//        model = new ResourceLocation(PandorasBox.MOD_ID, "block/pandoras_box.b3d");
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int packedLightIn) {

        matrixStack.pushPose();
        matrixStack.translate(entity.getX(), entity.getY() + MathHelper.sin((entity.tickCount + partialTicks) * 0.04f) * 0.05, entity.getZ());
        matrixStack.mulPose(new Quaternion(0.0F, 1.0F, 0.0F, -entityYaw));

        PBEffect effect = entity.getBoxEffect();
        boolean flag = !entity.isInvisible();
        boolean flag1 = !flag && !entity.isInvisibleTo(Minecraft.getInstance().player);
        IVertexBuilder builder = renderTypeBuffer.getBuffer(Objects.requireNonNull(getRenderType(entity, flag, flag1)));
        if (/*effect != null && */!effect.isDone(entity, entity.getEffectTicksExisted()) && entity.getDeathTicks() < 0)
        {
            PBEffectRenderer renderer = PBEffectRenderingRegistry.rendererForEffect(effect);
            if (renderer != null)
                renderer.renderBox(entity, effect, partialTicks, matrixStack, builder);
        }

        if (!entity.isInvisible())
        {
            float boxScale = entity.getCurrentScale();
            if (boxScale < 1.0f)
                matrixStack.scale(boxScale, boxScale, boxScale);

            matrixStack.translate(0.0f, 1.5f, 0.0f);
            matrixStack.mulPose(new Quaternion(0.0f, 0.0f, 1.0f, 180.0f));
            /*ArrowEntity emptyEntity = new ArrowEntity(entity.level, entity.getX(), entity.getY(), entity.getZ());
            emptyEntity.xRot = entity.getRatioBoxOpen(partialTicks) * 120.0f / 180.0f * 3.1415926f;*/
            int i = getPackedOverlay(0);
            model.renderToBuffer(matrixStack, builder, packedLightIn, i,1,1, 1, flag1 ? 0.15F : 1);
            model.setupAnim(entity, 0, 0, 0, 0, 0);
        }

        matrixStack.popPose();

        super.render(entity, entityYaw, partialTicks, matrixStack, renderTypeBuffer, packedLightIn);
    }
    public static int getPackedOverlay(float uIn) {
        return OverlayTexture.pack(OverlayTexture.u(uIn), OverlayTexture.v(false));
    }
    @Nullable
    protected RenderType getRenderType(T entity, boolean normal, boolean translucent) {
        ResourceLocation resourcelocation = this.getTextureLocation(entity);
        if (translucent) {
            return RenderType.entityTranslucent(resourcelocation);
        } else if (normal) {
            return this.model.renderType(resourcelocation);
        } else {
            return entity.isGlowing() ? RenderType.outline(resourcelocation) : null;
        }
    }
    @Override
    public ResourceLocation getTextureLocation(T var1)
    {
        return texture;
    }

    @Override
    public EntityRenderer<? super T> createRenderFor(EntityRendererManager manager) {
        return null;
    }
}
