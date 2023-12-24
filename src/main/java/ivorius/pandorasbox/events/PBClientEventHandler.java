package ivorius.pandorasbox.events;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.client.rendering.PandorasBoxBlockEntityRenderer;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.init.Registry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Map;

import static ivorius.pandorasbox.PandorasBox.logger;

@Mod.EventBusSubscriber(modid = PandorasBox.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PBClientEventHandler {
    @SubscribeEvent
    public static void modelLayerLocationInit(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(PandorasBoxModel.LAYER_LOCATION, PandorasBoxModel::createBodyLayer);
    }
    @SubscribeEvent
    public static void entityRendererInit(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Registry.Box.get(), PandorasBoxRenderer::new);
        PandorasBoxBlockEntityRenderer.register(event);
    }
    @SubscribeEvent
    public static void addPB(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(Registry.PB.get());
        }
    }
}
