package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Quaternion;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class IvRenderHelper {

    public static void renderLights(float ticks, int color, float alpha, int number, MatrixStack matrixStack, IVertexBuilder builder)
    {
        float width = 2.5f;

        float r = (float)(color >> 16 & 255) / 255.0F;
        float g = (float)(color >> 8 & 255) / 255.0F;
        float b = (float)(color & 255) / 255.0F;

        BufferBuilder renderer = Tessellator.getInstance().getBuilder();

        float usedTicks = ticks / 200.0F;

        Random random = new Random(432L);

        for (int var7 = 0; (float) var7 < number; ++var7)
        {
            float xLogFunc = (((float) var7 / number * 28493.0f + ticks) / 10.0f) % 20.0f;
            if (xLogFunc > 10.0f)
            {
                xLogFunc = 20.0f - xLogFunc;
            }

            float yLogFunc = 1.0f / (1.0f + (float) Math.pow(2.71828f, -0.8f * xLogFunc) * ((1.0f / 0.01f) - 1.0f));

            float lightAlpha = yLogFunc;

            if (lightAlpha > 0.01f)
            {
                matrixStack.mulPose(new Quaternion(1.0F, 0.0F, 0.0F, random.nextFloat() * 360.0F));
                matrixStack.mulPose(new Quaternion(0.0F, 1.0F, 0.0F, random.nextFloat() * 360.0F));
                matrixStack.mulPose(new Quaternion(0.0F, 0.0F, 1.0F, random.nextFloat() * 360.0F));
                matrixStack.mulPose(new Quaternion(1.0F, 0.0F, 0.0F, random.nextFloat() * 360.0F));
                matrixStack.mulPose(new Quaternion(0.0F, 1.0F, 0.0F, random.nextFloat() * 360.0F));
                matrixStack.mulPose(new Quaternion(0.0F, 0.0F, 1.0F, random.nextFloat() * 360.0F + usedTicks * 90.0F));
                renderer.begin(GL11.GL_TRIANGLE_FAN, DefaultVertexFormats.POSITION_COLOR);
                float var8 = random.nextFloat() * 20.0F + 5.0F;
                float var9 = random.nextFloat() * 2.0F + 1.0F;
                renderer.color(r, g, b, alpha * lightAlpha);
                renderer.vertex(0.0D, 0.0D, 0.0D).endVertex();
                renderer.color(r, g, b, 0);
                renderer.vertex(-width * (double) var9, var8, (-0.5F * var9)).endVertex();
                renderer.vertex(width * (double) var9, var8, (-0.5F * var9)).endVertex();
                renderer.vertex(0.0D, var8, (1.0F * var9)).endVertex();
                renderer.vertex(-width * (double) var9, var8, (-0.5F * var9)).endVertex();
                Tessellator.getInstance().end();
            }
        }

        matrixStack.popPose();
        RenderSystem.depthMask(true);
        RenderSystem.disableCull();
        RenderSystem.disableBlend();
        builder.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableTexture();
        GL11.glEnable(GL11.GL_ALPHA_TEST);
    }
}
