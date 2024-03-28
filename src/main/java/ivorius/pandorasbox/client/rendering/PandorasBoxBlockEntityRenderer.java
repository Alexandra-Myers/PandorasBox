package ivorius.pandorasbox.client.rendering;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.block.PandorasBoxBlockEntity;
import ivorius.pandorasbox.init.Registry;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class PandorasBoxBlockEntityRenderer extends TileEntityRenderer<PandorasBoxBlockEntity> {
    public static final ResourceLocation texture = new ResourceLocation(PandorasBox.MOD_ID, "textures/entity/pandoras_box.png");
    private final ModelRenderer feet;
    private final ModelRenderer body;
    private final ModelRenderer joint;
    private final ModelRenderer top;
    public PandorasBoxBlockEntityRenderer(TileEntityRendererDispatcher p_i226006_1_) {
        super(p_i226006_1_);

        feet = new ModelRenderer(32, 32, 0, 0);
        feet.setPos(0.0F, 1.0F, 0.0F);
        feet.texOffs(0, 14).addBox(-3.5F, 22.0F, 2.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        feet.texOffs(4, 14).addBox(2.5F, 22.0F, 2.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        feet.texOffs(4, 16).addBox(2.5F, 22.0F, -3.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        feet.texOffs(0, 16).addBox(-3.5F, 22.0F, -3.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        body = new ModelRenderer(32, 32, 0, 0);
        body.setPos(0.0F, 24.0F, 0.0F);
        body.texOffs(0, 0).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 6.0F, 8.0F, 0.0F, false);

        joint = new ModelRenderer(32, 32, 0, 0);
        joint.setPos(0.0F, 24.0F, 0.0F);


        ModelRenderer cube_r1 = new ModelRenderer(32, 32, 0, 0);
        cube_r1.setPos(0.0F, -10.0F, -4.0F);
        joint.addChild(cube_r1);
        setRotationAngle(cube_r1, -0.3054F, 0.0F, 0.0F);
        cube_r1.texOffs(8, 14).addBox(-3.5F, 1.75F, 0.5F, 7.0F, 1.0F, 1.0F, 0.0F, false);

        top = new ModelRenderer(32, 32, 0, 0);
        top.setPos(0.0F, 16.0F, -4.0F);
        setRotationAngle(top, -0.0802F, 0.0F, 0.0F);
        top.texOffs(0, 18).addBox(-4.0F, -0.5F, 0.0F, 8.0F, 1.0F, 8.0F, 0.0F, false);
        top.texOffs(0, 27).addBox(-2.0F, -1.5F, 2.0F, 4.0F, 1.0F, 4.0F, 0.0F, false);
        top.texOffs(8, 16).addBox(2.0F, -1.0F, 3.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        top.texOffs(8, 16).addBox(-3.0F, -1.0F, 3.5F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        top.texOffs(8, 16).addBox(-0.5F, -1.0F, 6.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        top.texOffs(8, 16).addBox(-0.5F, -1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        top.texOffs(12, 16).addBox(3.25F, -0.75F, 7.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
        top.texOffs(16, 16).addBox(-4.25F, -0.75F, 7.25F, 1.0F, 1.0F, 1.0F, 0.0F, false);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.xRot = x;
        modelRenderer.yRot = y;
        modelRenderer.zRot = z;
    }
    @Override
    public void render(PandorasBoxBlockEntity blockEntity, float p_225616_2_, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225616_5_, int p_225616_6_) {
        IVertexBuilder builder = renderTypeBuffer.getBuffer(RenderType.entitySolid(texture));
        matrixStack.translate(0.5f, 1.5f, 0.5f);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0f));
        float yRot = blockEntity.getRotationYaw();
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(yRot));
        renderAll(matrixStack, builder, p_225616_5_, p_225616_6_);
    }
    public void renderAll(MatrixStack matrixStack, IVertexBuilder builder, int i, int i1) {
        feet.render(matrixStack, builder, i, i1);
        body.render(matrixStack, builder, i, i1);
        joint.render(matrixStack, builder, i, i1);
        top.render(matrixStack, builder, i, i1);
    }
    public static void register() {
        ClientRegistry.bindTileEntityRenderer(Registry.TEPB.get(), PandorasBoxBlockEntityRenderer::new);
    }
}
