package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.RandomSource;
import org.joml.Matrix4f;

public class IvRenderHelper {
    public static final float width = 2.5f;

    public static void renderLights(float ticks, float scale, int color, float alpha, int number, PoseStack poseStack, MultiBufferSource multiBufferSource) {
        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;
        float usedTicks = ticks / 200.0F;
        float m = Math.min(usedTicks > 0.8F ? (usedTicks - 0.8F) / 0.2F : 0.0F, 1.0F);
        RandomSource randomSource = RandomSource.create(432L);
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.lightning());
        poseStack.pushPose();
        poseStack.translate(0.0, 1.0, 0.0);
        poseStack.scale(scale, scale, scale);

        for(int n = 0; n < number; ++n) {
            float xLogFunc = (((float) n / number * 28493.0f + ticks) / 10.0f) % 20.0f;
            if (xLogFunc > 10.0f) {
                xLogFunc = 20.0f - xLogFunc;
            }

            float lightAlpha = 1.0f / (1.0f + (float) Math.pow(2.71828f, -0.8f * xLogFunc) * ((1.0f / 0.01f) - 1.0f));
            if (lightAlpha > 0.01f) {
                poseStack.mulPose(Axis.XP.rotationDegrees(randomSource.nextFloat() * 360.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(randomSource.nextFloat() * 360.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(randomSource.nextFloat() * 360.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(randomSource.nextFloat() * 360.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(randomSource.nextFloat() * 360.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(randomSource.nextFloat() * 360.0F + usedTicks * 90.0F));
                float o = randomSource.nextFloat() * 20.0F + 5.0F + m * 10.0F;
                float p = randomSource.nextFloat() * 2.0F + 1.0F + m * 2.0F;
                Matrix4f matrix4f = poseStack.last().pose();
                baseVertex(vertexConsumer, matrix4f, r, g, b, (int) (alpha * lightAlpha));
                vertex(vertexConsumer, matrix4f, r, g, b, o, p);
                vertex1(vertexConsumer, matrix4f, r, g, b, o, p);
                baseVertex(vertexConsumer, matrix4f, r, g, b, (int) (alpha * lightAlpha));
                vertex1(vertexConsumer, matrix4f, r, g, b, o, p);
                vertex2(vertexConsumer, matrix4f, r, g, b, o, p);
                baseVertex(vertexConsumer, matrix4f, r, g, b, (int) (alpha * lightAlpha));
                vertex2(vertexConsumer, matrix4f, r, g, b, o, p);
                vertex(vertexConsumer, matrix4f, r, g, b, o, p);
            }
        }
        poseStack.popPose();
    }
    private static void baseVertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, int r, int g, int b, int alpha) {
        vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(r, g, b, alpha).endVertex();
    }
    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, int r, int g, int b, float y, float x) {
        vertexConsumer.vertex(matrix4f, -width * x, y, -0.5F * x).color(r, g, b, 0).endVertex();
    }

    private static void vertex1(VertexConsumer vertexConsumer, Matrix4f matrix4f, int r, int g, int b, float y, float x) {
        vertexConsumer.vertex(matrix4f, width * x, y, -0.5F * x).color(r, g, b, 0).endVertex();
    }

    private static void vertex2(VertexConsumer vertexConsumer, Matrix4f matrix4f, int r, int g, int b, float y, float x) {
        vertexConsumer.vertex(matrix4f, 0.0F, y, x).color(r, g, b, 0).endVertex();
    }
}
