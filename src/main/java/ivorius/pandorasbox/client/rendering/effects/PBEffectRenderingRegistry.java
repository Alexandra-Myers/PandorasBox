package ivorius.pandorasbox.client.rendering.effects;

import ivorius.pandorasbox.client.rendering.effects.renderstate.PandoraEffectRenderState;
import ivorius.pandorasbox.effects.PBEffect;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lukas on 05.12.14.
 */
public class PBEffectRenderingRegistry {
    private static final PBEffectRenderer<PBEffect, PandoraEffectRenderState> DEFAULT = new PBEffectRenderer<>();
    private static final Map<Identifier, PBEffectRenderer<?, ?>> renderers = new HashMap<>();

    public static <PE extends PBEffect, PERS extends PandoraEffectRenderState> void registerRenderer(Identifier identifier, PBEffectRenderer<PE, PERS> renderer) {
        renderers.put(identifier, renderer);
    }

    public static PBEffectRenderer<?, ?> rendererForEffect(PandoraEffectRenderState renderState) {
        return rendererForID(renderState.renderer);
    }
    public static PBEffectRenderer<?, ?> rendererForID(Identifier identifier) {
        PBEffectRenderer<?, ?> renderer = renderers.get(identifier);
        if (renderer != null)
            return renderer;
        return DEFAULT;
    }
}
