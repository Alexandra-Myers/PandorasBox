package ivorius.pandorasbox.events;

import ivorius.pandorasbox.PandorasBox;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

import static ivorius.pandorasbox.events.PBEventHandler.initPB;

@Mod.EventBusSubscriber(modid = PandorasBox.MOD_ID, value = Dist.CLIENT)
public class ForgeClientEventHandler {
    @SubscribeEvent
    public static void clientLogin(ClientPlayerNetworkEvent.LoggingIn event) {
        initPB();
    }
}
