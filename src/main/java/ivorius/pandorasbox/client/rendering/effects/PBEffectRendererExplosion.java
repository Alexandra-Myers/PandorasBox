package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import ivorius.pandorasbox.effects.PBEffectExplode;
import ivorius.pandorasbox.entitites.EntityPandorasBox;
import net.minecraft.client.renderer.*;

/**
 * Created by lukas on 05.12.14.
 */
public class PBEffectRendererExplosion implements PBEffectRenderer<PBEffectExplode>
{
    @Override
    public void renderBox(EntityPandorasBox entity, PBEffectExplode effect, float partialTicks, MatrixStack matrixStack, IVertexBuilder builder)
    {
        if (!entity.isInvisible())
        {
            int lightColor = effect.burning ? 0xff0088 : 0xdd3377;

            float timePassed = (float) entity.getEffectTicksExisted() / (float) effect.maxTicksAlive;
            timePassed *= timePassed;
            timePassed *= timePassed;

            float scale = (timePassed * 0.3f) * effect.explosionRadius * 0.3f;

            matrixStack.pushPose();
            matrixStack.translate(0.0f, 0.2f, 0.0f);
            matrixStack.scale(scale, scale, scale);
            IvRenderHelper.renderLights(entity.tickCount + partialTicks, lightColor, timePassed, 10, matrixStack, builder);
            matrixStack.popPose();
        }
    }
}
