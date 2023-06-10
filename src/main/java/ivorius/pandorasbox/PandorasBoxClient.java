package ivorius.pandorasbox;

import ivorius.pandorasbox.client.rendering.PandorasBoxBlockEntityRenderer;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.init.Registry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PandorasBox.MOD_ID)
@OnlyIn(Dist.CLIENT)
public class PandorasBoxClient {
    public final IEventBus EVENT_BUS;
    PandorasBoxClient() {
        EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        Registry.init(EVENT_BUS);
        EVENT_BUS.addListener(this::clientInit);
    }
    public void clientInit(FMLClientSetupEvent event) {
        EntityRenderers.register(Registry.Box.get(), PandorasBoxRenderer::new);
        PandorasBoxBlockEntityRenderer.register();
    }
}
