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
import ivorius.pandorasbox.init.ItemInit;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.renderer.entity.GiantMobRenderer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PandorasBox.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PandorasBoxClient {
    public static Overlay cached = null;
    public static void clientInit() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> PandorasBoxHelper.initialize());
        ClientPlayNetworking.registerGlobalReceiver(PandorasBox.ClientboundUpdateFakeDeathPacket.TYPE, (packet, player, responseSender) -> {
            if (Minecraft.getInstance().getOverlay() != null) {
                PandorasBoxClient.cached = new FakeDeathOverlay(new DeathScreen(null, player.level().getLevelData().isHardcore()));
            } else {
                Minecraft.getInstance().setOverlay(new FakeDeathOverlay(new DeathScreen(null, player.level().getLevelData().isHardcore())));
            }
        });
    }

    public static RegistryAccess tryGetClientRegistryAccess() {
        if (Minecraft.getInstance().level == null) return null;
        return Minecraft.getInstance().level.registryAccess();
    }

    @SubscribeEvent
    public static void onCreativeTabBuild(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS)) event.accept(ItemInit.PBI);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.GIANT.get(), context -> new GiantMobRenderer(context, 6.0F));
        event.registerEntityRenderer(EntityInit.BOX.get(), PandorasBoxRenderer::new);

        event.registerBlockEntityRenderer(BlockEntityInit.BEPB.get(), PandorasBoxBlockEntityRenderer::new);

        PBEffectRenderingRegistry.registerRenderer(PBEffect.DEFAULT, new PBEffectRenderer<>());
        PBEffectRenderingRegistry.registerRenderer(PBEffectExplode.EXPLODE, new PBEffectRendererExplosion());
        PBEffectRenderingRegistry.registerRenderer(PBEffectMeltdown.MELTDOWN, new PBEffectRendererMeltdown());
        PBEffectRenderingRegistry.registerRenderer(PBEffectMulti.MULTI, new PBEffectRendererMulti());
    }

    @SubscribeEvent
    public static void registerModelLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(PandorasBoxModel.LAYER_LOCATION, PandorasBoxModel::createBodyLayer);
    }
}
