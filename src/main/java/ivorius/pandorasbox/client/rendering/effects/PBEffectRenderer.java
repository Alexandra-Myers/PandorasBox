package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.client.renderer.MultiBufferSource;

/**
 * Created by lukas on 05.12.14.
 */
public interface PBEffectRenderer<E extends PBEffect> {
    void renderBox(PandorasBoxRenderer<?> renderer, PandorasBoxEntity entity, E effect, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, VertexConsumer consumer, int packedLightIn);
}
