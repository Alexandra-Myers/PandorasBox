package ivorius.pandorasbox.events;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.client.rendering.PandorasBoxBlockEntityRenderer;
import ivorius.pandorasbox.client.rendering.PandorasBoxModel;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.init.Registry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
            event.accept(Registry.PB);
        }
    }
}
