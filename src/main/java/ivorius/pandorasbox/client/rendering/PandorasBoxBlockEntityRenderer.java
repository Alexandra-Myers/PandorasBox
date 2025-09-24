package ivorius.pandorasbox.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.block.PandorasBoxBlockEntity;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Set;

public class PandorasBoxBlockEntityRenderer implements BlockEntityRenderer<PandorasBoxBlockEntity> {
    public static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "textures/entity/pandoras_box.png");
    public final PandorasBoxModel model;
    public PandorasBoxBlockEntityRenderer(BlockEntityRendererProvider.Context berpContext) {
        this.model = new PandorasBoxModel(berpContext.bakeLayer(PandorasBoxModel.LAYER_LOCATION));
    }
    public PandorasBoxBlockEntityRenderer(EntityModelSet entityModelSet) {
        this.model = new PandorasBoxModel(entityModelSet.bakeLayer(PandorasBoxModel.LAYER_LOCATION));
    }

    @Override
    public void render(PandorasBoxBlockEntity blockEntity, float f, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, Vec3 vec3) {
        render(poseStack, multiBufferSource, blockEntity.getRotationYaw(), i, j);
    }
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, float yRot, int i, int j) {
        VertexConsumer builder = multiBufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));
        poseStack.pushPose();
        poseStack.translate(0.5f, 1.5f, 0.5f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        this.model.renderToBuffer(poseStack, builder, i, j, 0xFFFFFFFF);
        poseStack.popPose();
    }
    public void renderItem(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j) {
        render(poseStack, multiBufferSource, 0, i, j);
    }

    public void getExtents(Set<Vector3f> set) {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        poseStack.mulPose(Axis.YP.rotationDegrees(0));
        this.model.root().getExtentsForGui(poseStack, set);
    }
}
