package ivorius.pandorasbox.events;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.client.PBSpriteSourceProvider;
import ivorius.pandorasbox.client.rendering.PandorasBoxBlockEntityRenderer;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.init.Registry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = PandorasBox.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class PBClientEventHandler {
    @SubscribeEvent
    public void clientInit(FMLClientSetupEvent event) {
        EntityRenderers.register(Registry.Box.get(), PandorasBoxRenderer::new);
        PandorasBoxBlockEntityRenderer.register();
    }
    @SubscribeEvent
    public void addPB(BuildCreativeModeTabContentsEvent event) {
        if(event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(Registry.PB);
        }
    }
    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();

        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        gen.addProvider(event.includeClient(), new PBSpriteSourceProvider(packOutput, existingFileHelper));
    }
}
