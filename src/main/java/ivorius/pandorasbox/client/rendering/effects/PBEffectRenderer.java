package ivorius.pandorasbox.client.rendering.effects;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;

/**
 * Created by lukas on 05.12.14.
 */
public interface PBEffectRenderer<E extends PBEffect> {
    void renderBox(PandorasBoxEntity entity, E effect, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, IVertexBuilder builder);
}
