package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.effects.PBEffectExplode;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import java.util.List;

/**
 * Created by lukas on 05.12.14.
 */
public class PBEffectRendererExplosion implements PBEffectRenderer<PBEffectExplode> {
    @Override
    public void renderBox(PandorasBoxRenderer renderer, PandorasBoxEntity entity, PBEffectExplode effect, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, VertexConsumer consumer, int packedLightIn) {
        int lightColor = effect.burning ? 0xff0088 : 0xbb3399;

        float timePassed = Math.min((float) entity.getEffectTicksExisted() / (float) effect.maxTicksAlive, 1F);
        timePassed *= timePassed;
        timePassed *= timePassed;

        float scale = (timePassed * 0.3f) * effect.explosionRadius * 0.3f;
        IvRenderHelper.renderLights(entity.tickCount + partialTicks, scale, lightColor, timePassed * 255F, 10, poseStack, multiBufferSource);
    }

    @Override
    public List<RenderLayer<PandorasBoxEntity, PandorasBoxModel>> getLayers(PandorasBoxRenderer renderer, PandorasBoxEntity entity, PBEffectExplode effect, PandorasBoxModel model, float partialTicks) {
        return null;
    }
}
