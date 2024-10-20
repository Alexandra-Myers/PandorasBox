package ivorius.pandorasbox;

import ivorius.pandorasbox.client.rendering.PandorasBoxBlockEntityRenderer;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRendererExplosion;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRendererMeltdown;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRendererMulti;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRenderingRegistry;
import ivorius.pandorasbox.effects.PBEffectExplode;
import ivorius.pandorasbox.effects.PBEffectMeltdown;
import ivorius.pandorasbox.effects.PBEffectMulti;
import ivorius.pandorasbox.init.BlockEntityInit;
import ivorius.pandorasbox.init.EntityInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import static ivorius.pandorasbox.PandorasBox.initPB;

public class PandorasBoxClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(EntityInit.BOX, PandorasBoxRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(PandorasBoxModel.LAYER_LOCATION, PandorasBoxModel::createBodyLayer);
        BlockEntityRenderers.register(BlockEntityInit.BEPB, PandorasBoxBlockEntityRenderer::new);
        PBEffectRenderingRegistry.registerRenderer(PBEffectExplode.class, new PBEffectRendererExplosion());
        PBEffectRenderingRegistry.registerRenderer(PBEffectMeltdown.class, new PBEffectRendererMeltdown());
        PBEffectRenderingRegistry.registerRenderer(PBEffectMulti.class, new PBEffectRendererMulti());
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> initPB());
    }
}
