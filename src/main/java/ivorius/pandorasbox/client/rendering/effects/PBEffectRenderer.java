package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import java.util.List;

/**
 * Created by lukas on 05.12.14.
 */
public interface PBEffectRenderer<E extends PBEffect> {
    void renderBox(PandorasBoxRenderer renderer, PandorasBoxEntity entity, E effect, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, VertexConsumer consumer, int packedLightIn);

    List<RenderLayer<PandorasBoxEntity, PandorasBoxModel>> getLayers(PandorasBoxRenderer renderer, PandorasBoxEntity entity, E effect, PandorasBoxModel model, float partialTicks);
}
