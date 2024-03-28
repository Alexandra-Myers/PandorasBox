package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import ivorius.pandorasbox.effects.PBEffectExplode;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;

/**
 * Created by lukas on 05.12.14.
 */
public class PBEffectRendererExplosion implements PBEffectRenderer<PBEffectExplode> {
    @Override
    public void renderBox(PandorasBoxEntity entity, PBEffectExplode effect, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder builder) {
        int lightColor = effect.burning ? 0xff0088 : 0xbb3399;

        float timePassed = Math.min((float) entity.getEffectTicksExisted() / (float) effect.maxTicksAlive, 1F);
        timePassed *= timePassed;
        timePassed *= timePassed;

        float scale = (timePassed * 0.3f) * effect.explosionRadius * 0.3f;
        IvRenderHelper.renderLights(entity.tickCount + partialTicks, scale, lightColor, timePassed * 255F, 10, matrixStack, renderTypeBuffer);
    }
}
