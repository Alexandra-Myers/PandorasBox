package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Random;

public class IvRenderHelper {
    public static final float width = 2.5f;

    public static void renderLights(float ticks, float scale, int color, float alpha, int number, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer) {
        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;
        float usedTicks = ticks / 200.0F;
        float m = Math.min(usedTicks > 0.8F ? (usedTicks - 0.8F) / 0.2F : 0.0F, 1.0F);
        Random random = new Random(432L);
        IVertexBuilder vertexBuilder = renderTypeBuffer.getBuffer(RenderType.lightning());
        matrixStack.pushPose();
        matrixStack.translate(0.0, 1.0, 0.0);
        matrixStack.scale(scale, scale, scale);

        for(int n = 0; n < number; ++n) {
            float xLogFunc = (((float) n / number * 28493.0f + ticks) / 10.0f) % 20.0f;
            if (xLogFunc > 10.0f) {
                xLogFunc = 20.0f - xLogFunc;
            }

            float lightAlpha = 1.0f / (1.0f + (float) Math.pow(2.71828f, -0.8f * xLogFunc) * ((1.0f / 0.01f) - 1.0f));
            if (lightAlpha > 0.01f) {
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.mulPose(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.mulPose(Vector3f.XP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.mulPose(Vector3f.YP.rotationDegrees(random.nextFloat() * 360.0F));
                matrixStack.mulPose(Vector3f.ZP.rotationDegrees(random.nextFloat() * 360.0F + usedTicks * 90.0F));
                float o = random.nextFloat() * 20.0F + 5.0F + m * 10.0F;
                float p = random.nextFloat() * 2.0F + 1.0F + m * 2.0F;
                Matrix4f matrix4f = matrixStack.last().pose();
                baseVertex(vertexBuilder, matrix4f, r, g, b, (int) (alpha * lightAlpha));
                vertex(vertexBuilder, matrix4f, r, g, b, o, p);
                vertex1(vertexBuilder, matrix4f, r, g, b, o, p);
                baseVertex(vertexBuilder, matrix4f, r, g, b, (int) (alpha * lightAlpha));
                vertex1(vertexBuilder, matrix4f, r, g, b, o, p);
                vertex2(vertexBuilder, matrix4f, r, g, b, o, p);
                baseVertex(vertexBuilder, matrix4f, r, g, b, (int) (alpha * lightAlpha));
                vertex2(vertexBuilder, matrix4f, r, g, b, o, p);
                vertex(vertexBuilder, matrix4f, r, g, b, o, p);
            }
        }
        matrixStack.popPose();
    }
    private static void baseVertex(IVertexBuilder vertexBuilder, Matrix4f matrix4f, int r, int g, int b, int alpha) {
        vertexBuilder.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(r, g, b, alpha).endVertex();
    }
    private static void vertex(IVertexBuilder vertexBuilder, Matrix4f matrix4f, int r, int g, int b, float y, float x) {
        vertexBuilder.vertex(matrix4f, -width * x, y, -0.5F * x).color(r, g, b, 0).endVertex();
    }

    private static void vertex1(IVertexBuilder vertexBuilder, Matrix4f matrix4f, int r, int g, int b, float y, float x) {
        vertexBuilder.vertex(matrix4f, width * x, y, -0.5F * x).color(r, g, b, 0).endVertex();
    }

    private static void vertex2(IVertexBuilder vertexBuilder, Matrix4f matrix4f, int r, int g, int b, float y, float x) {
        vertexBuilder.vertex(matrix4f, 0.0F, y, x).color(r, g, b, 0).endVertex();
    }
}
