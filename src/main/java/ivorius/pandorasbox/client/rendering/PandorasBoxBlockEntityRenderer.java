package ivorius.pandorasbox.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.block.PandorasBoxBlockEntity;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class PandorasBoxBlockEntityRenderer implements BlockEntityRenderer<PandorasBoxBlockEntity> {
    public static final ResourceLocation PANDORAS_BOX = ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "textures/entity/pandoras_box.png");
    public final PandorasBoxModel model;
    public static final float DEFAULT_X_ROT = (float) (-0.025F * 2F / 3F * Math.PI);
    public PandorasBoxBlockEntityRenderer(BlockEntityRendererProvider.Context berpContext) {
        this.model = new PandorasBoxModel(berpContext.bakeLayer(PandorasBoxModel.LAYER_LOCATION));
    }
    public PandorasBoxBlockEntityRenderer(EntityModelSet entityModelSet) {
        this.model = new PandorasBoxModel(entityModelSet.bakeLayer(PandorasBoxModel.LAYER_LOCATION));
    }
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, float yRot, int packedLightIn, int overlayTexture, boolean hasFoil) {
        poseStack.pushPose();
        poseStack.translate(0.5f, 1.5f, 0.5f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        this.model.setupAnim(DEFAULT_X_ROT);
        RenderType renderType = RenderType.entityCutoutNoCull(PANDORAS_BOX);
        VertexConsumer buffer = ItemRenderer.getFoilBuffer(multiBufferSource, renderType, false, hasFoil);
        this.model.renderToBuffer(poseStack, buffer, packedLightIn, overlayTexture, 0xFFFFFFFF);
        poseStack.popPose();
    }
    public void renderItem(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLightIn, int overlayTexture, boolean hasFoil) {
        render(poseStack, multiBufferSource, 0, packedLightIn, overlayTexture, hasFoil);
    }

    @Override
    public void render(PandorasBoxBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLightIn, int overlayTexture) {
        render(poseStack, multiBufferSource, blockEntity.getRotationYaw(), packedLightIn, overlayTexture, !blockEntity.getEnchantments().isEmpty());
    }
}
