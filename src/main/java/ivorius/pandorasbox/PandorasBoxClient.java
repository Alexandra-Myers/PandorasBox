package ivorius.pandorasbox;

import ivorius.pandorasbox.client.rendering.FakeDeathOverlay;
import ivorius.pandorasbox.client.rendering.PandorasBoxBlockEntityRenderer;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.client.rendering.effects.*;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectExplode;
import ivorius.pandorasbox.effects.PBEffectMeltdown;
import ivorius.pandorasbox.effects.PBEffectMulti;
import ivorius.pandorasbox.init.BlockEntityInit;
import ivorius.pandorasbox.init.EntityInit;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import static ivorius.pandorasbox.PandorasBox.initPB;

public class PandorasBoxClient implements ClientModInitializer {
    public static Overlay cached = null;
    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(EntityInit.BOX, PandorasBoxRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(PandorasBoxModel.LAYER_LOCATION, PandorasBoxModel::createBodyLayer);
        BlockEntityRenderers.register(BlockEntityInit.BEPB, PandorasBoxBlockEntityRenderer::new);
        PBEffectRenderingRegistry.registerRenderer(PBEffect.DEFAULT, new PBEffectRenderer<>());
        PBEffectRenderingRegistry.registerRenderer(PBEffectExplode.EXPLODE, new PBEffectRendererExplosion());
        PBEffectRenderingRegistry.registerRenderer(PBEffectMeltdown.MELTDOWN, new PBEffectRendererMeltdown());
        PBEffectRenderingRegistry.registerRenderer(PBEffectMulti.MULTI, new PBEffectRendererMulti());
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> initPB());
        ClientPlayNetworking.registerGlobalReceiver(PandorasBox.ClientboundUpdateFakeDeathPacket.TYPE, (clientboundUpdateFakeDeathPacket, context) -> {
            if (context.client().getOverlay() != null) {
                cached = new FakeDeathOverlay(new DeathScreen(null, context.player().level().getLevelData().isHardcore()));
            } else {
                context.client().setOverlay(new FakeDeathOverlay(new DeathScreen(null, context.player().level().getLevelData().isHardcore())));
            }
        });
    }
}
