package ivorius.pandorasbox;

import ivorius.pandorasbox.client.rendering.PandorasBoxBlockEntityRenderer;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRendererExplosion;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRenderingRegistry;
import ivorius.pandorasbox.effects.PBEffectExplode;
import ivorius.pandorasbox.init.BlockEntityInit;
import ivorius.pandorasbox.init.EntityInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import static ivorius.pandorasbox.PandorasBox.initPB;
import static ivorius.pandorasbox.PandorasBox.logger;

public class PandorasBoxClient implements ClientModInitializer {
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(EntityInit.Box, PandorasBoxRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(PandorasBoxModel.LAYER_LOCATION, PandorasBoxModel::createBodyLayer);
        BlockEntityRenderers.register(BlockEntityInit.BEPB, PandorasBoxBlockEntityRenderer::new);
        PBEffectRenderingRegistry.registerRenderer(PBEffectExplode.class, new PBEffectRendererExplosion());
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> initPB());
        ClientPlayNetworking.registerGlobalReceiver(PandorasBox.AtlasConfigPacket.TYPE, (packet, player, responseSender) -> logger.info("Loading config details from buffer. Config: " + packet.config().name.toString()));
    }
}
