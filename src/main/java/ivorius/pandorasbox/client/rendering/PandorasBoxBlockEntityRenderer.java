package ivorius.pandorasbox.client.rendering;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.block.PandorasBoxBlockEntity;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;

public class PandorasBoxBlockEntityRenderer implements BlockEntityRenderer<PandorasBoxBlockEntity, PandorasBoxBlockEntityRenderState> {
    public static final ResourceLocation PANDORAS_BOX = ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, "textures/entity/pandoras_box.png");
    public final PandorasBoxModel model;
    public PandorasBoxBlockEntityRenderer(BlockEntityRendererProvider.Context berpContext) {
        this.model = new PandorasBoxModel(berpContext.bakeLayer(PandorasBoxModel.LAYER_LOCATION));
    }
    public PandorasBoxBlockEntityRenderer(EntityModelSet entityModelSet) {
        this.model = new PandorasBoxModel(entityModelSet.bakeLayer(PandorasBoxModel.LAYER_LOCATION));
    }
    public void render(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, float yRot, int packedLightIn, int overlayTexture, int outlineColor, boolean hasFoil, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5f, 1.5f, 0.5f);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        this.model.setupAnim(PandorasBoxRenderState.BLOCK_ENTITY_STATE);
        submitNodeCollector.submitModelPart(this.model.root(), poseStack, RenderType.entityCutoutNoCull(PANDORAS_BOX), packedLightIn, overlayTexture, null, false, hasFoil, -1, crumblingOverlay, outlineColor);
        poseStack.popPose();
    }
    public void renderItem(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLightIn, int overlayTexture, int outlineColor, boolean hasFoil) {
        render(poseStack, submitNodeCollector, 0, packedLightIn, overlayTexture, outlineColor, hasFoil, null);
    }

    public void getExtents(Set<Vector3f> set) {
        PoseStack poseStack = new PoseStack();
        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        poseStack.mulPose(Axis.YP.rotationDegrees(0));
        this.model.root().getExtentsForGui(poseStack, set);
    }

    @Override
    public PandorasBoxBlockEntityRenderState createRenderState() {
        return new PandorasBoxBlockEntityRenderState();
    }

    @Override
    public void submit(PandorasBoxBlockEntityRenderState renderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        render(poseStack, submitNodeCollector, renderState.rotationYaw, renderState.lightCoords, OverlayTexture.NO_OVERLAY, 0, renderState.hasFoil, renderState.breakProgress);
    }

    @Override
    public void extractRenderState(PandorasBoxBlockEntity blockEntity, PandorasBoxBlockEntityRenderState blockEntityRenderState, float f, Vec3 vec3, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, blockEntityRenderState, f, vec3, crumblingOverlay);
        blockEntityRenderState.rotationYaw = blockEntity.getRotationYaw();
        blockEntityRenderState.hasFoil = !blockEntity.getEnchantments().isEmpty();
    }
}
