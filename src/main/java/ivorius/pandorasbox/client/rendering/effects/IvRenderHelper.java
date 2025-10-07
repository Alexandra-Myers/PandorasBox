package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.RandomSource;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class IvRenderHelper {
    public static final float width = 2.5f;

    public static void renderLights(float ticks, float scale, float height, int color, float alpha, int number, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        submitRays(ticks, scale, height, color, alpha, number, poseStack, submitNodeCollector, RenderType.dragonRays());
        submitRays(ticks, scale, height, color, alpha, number, poseStack, submitNodeCollector, RenderType.dragonRaysDepth());
    }
    private static void submitRays(float ticks, float scale, float height, int color, float alpha, int number, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, RenderType renderType) {
        submitNodeCollector.submitCustomGeometry(
                poseStack,
                renderType,
                (pose, vertexConsumer) -> {
                    float progress = ticks / 200.0F;
                    float g = Math.min(progress > 0.8F ? (progress - 0.8F) / 0.2F : 0.0F, 1.0F);
                    int red = color >> 16 & 255;
                    int green = color >> 8 & 255;
                    int blue = color & 255;
                    RandomSource randomSource = RandomSource.create(432L);
                    Vector3f vector3f = new Vector3f();
                    Vector3f vector3f2 = new Vector3f();
                    Vector3f vector3f3 = new Vector3f();
                    Vector3f vector3f4 = new Vector3f();
                    Quaternionf quaternionf = new Quaternionf();
                    pose.translate(0.0F, height + 0.1F, 0.0F);
                    pose.scale(scale, scale, scale);

                    for (int n = 0; n < number; n++) {
                        float xLogFunc = (((float) n / number * 28493.0f + ticks) / 10.0f) % 20.0f;
                        if (xLogFunc > 10.0f) {
                            xLogFunc = 20.0f - xLogFunc;
                        }
                        float lightAlpha = 1.0f / (1.0f + (float) Math.pow(2.71828f, -0.8f * xLogFunc) * ((1.0f / 0.01f) - 1.0f));
                        if (lightAlpha > 0.01f) {
                            quaternionf.rotationXYZ(
                                            randomSource.nextFloat() * (float) (Math.PI * 2), randomSource.nextFloat() * (float) (Math.PI * 2), randomSource.nextFloat() * (float) (Math.PI * 2)
                                    )
                                    .rotateXYZ(
                                            randomSource.nextFloat() * (float) (Math.PI * 2),
                                            randomSource.nextFloat() * (float) (Math.PI * 2),
                                            randomSource.nextFloat() * (float) (Math.PI * 2) + progress * (float) (Math.PI / 2)
                                    );
                            pose.rotate(quaternionf);
                            float h = randomSource.nextFloat() * 20.0F + 5.0F + g * 10.0F;
                            float m = randomSource.nextFloat() * 2.0F + 1.0F + g * 2.0F;
                            vector3f2.set(-width * m, h, -0.5F * m);
                            vector3f3.set(width * m, h, -0.5F * m);
                            vector3f4.set(0.0F, h, m);
                            int finalAlpha = (int) (alpha * lightAlpha);
                            vertexConsumer.addVertex(pose, vector3f).setColor(red, green, blue, finalAlpha);
                            vertexConsumer.addVertex(pose, vector3f2).setColor(red, green, blue, 0);
                            vertexConsumer.addVertex(pose, vector3f3).setColor(red, green, blue, 0);
                            vertexConsumer.addVertex(pose, vector3f).setColor(red, green, blue, finalAlpha);
                            vertexConsumer.addVertex(pose, vector3f3).setColor(red, green, blue, 0);
                            vertexConsumer.addVertex(pose, vector3f4).setColor(red, green, blue, 0);
                            vertexConsumer.addVertex(pose, vector3f).setColor(red, green, blue, finalAlpha);
                            vertexConsumer.addVertex(pose, vector3f4).setColor(red, green, blue, 0);
                            vertexConsumer.addVertex(pose, vector3f2).setColor(red, green, blue, 0);
                        }
                    }
                }
        );
    }
}
