package ivorius.pandorasbox.client.rendering.effects;

import ivorius.pandorasbox.client.rendering.effects.renderstate.PandoraEffectRenderState;
import ivorius.pandorasbox.effects.PBEffect;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lukas on 05.12.14.
 */
public class PBEffectRenderingRegistry {
    private static final PBEffectRenderer<PBEffect, PandoraEffectRenderState> DEFAULT = new PBEffectRenderer<>();
    private static final Map<ResourceLocation, PBEffectRenderer<?, ?>> renderers = new HashMap<>();

    public static <PE extends PBEffect, PERS extends PandoraEffectRenderState> void registerRenderer(ResourceLocation resourceLocation, PBEffectRenderer<PE, PERS> renderer) {
        renderers.put(resourceLocation, renderer);
    }

    public static PBEffectRenderer<?, ?> rendererForEffect(PandoraEffectRenderState renderState) {
        return rendererForRL(renderState.renderer);
    }
    public static PBEffectRenderer<?, ?> rendererForRL(ResourceLocation resourceLocation) {
        PBEffectRenderer<?, ?> renderer = renderers.get(resourceLocation);
        if (renderer != null)
            return renderer;
        return DEFAULT;
    }
}
