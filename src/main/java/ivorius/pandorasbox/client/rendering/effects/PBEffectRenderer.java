package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;

/**
 * Created by lukas on 05.12.14.
 */
public interface PBEffectRenderer<E extends PBEffect>
{
    void renderBox(PandorasBoxEntity entity, E effect, float partialTicks, PoseStack matrixStack, VertexConsumer consumer);
}
