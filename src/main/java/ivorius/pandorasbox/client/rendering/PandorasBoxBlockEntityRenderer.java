package ivorius.pandorasbox.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.block.PandorasBoxBlockEntity;
import ivorius.pandorasbox.init.Registry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class PandorasBoxBlockEntityRenderer implements BlockEntityRenderer<PandorasBoxBlockEntity> {
    public static final ResourceLocation texture = new ResourceLocation(PandorasBox.MOD_ID, "textures/entity/pandoras_box.png");
    public final PandorasBoxBlockEntityModel model;
    public PandorasBoxBlockEntityRenderer(BlockEntityRendererProvider.Context p_i226006_1_) {
        model = new PandorasBoxBlockEntityModel(p_i226006_1_.bakeLayer(PandorasBoxModel.LAYER_LOCATION));
    }
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Registry.TEPB.get(), PandorasBoxBlockEntityRenderer::new);
    }

    @Override
    public void render(PandorasBoxBlockEntity blockEntity, float p_112308_, PoseStack matrixStack, MultiBufferSource multiBufferSource, int p_112311_, int p_112312_) {
        VertexConsumer builder = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));
        matrixStack.pushPose();
        matrixStack.translate(0.5f, 1.5f, 0.5f);
        matrixStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        float yRot = blockEntity.getRotationYaw();
        matrixStack.mulPose(Axis.YP.rotationDegrees(yRot));
        model.renderToBuffer(matrixStack, builder, p_112311_, p_112312_, 1,1,1,1);
        matrixStack.popPose();
    }
}
