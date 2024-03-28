package ivorius.pandorasbox.events;

import ivorius.pandorasbox.PandorasBox;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static ivorius.pandorasbox.events.PBEventHandler.initPB;

@Mod.EventBusSubscriber(modid = PandorasBox.MOD_ID, value = Dist.CLIENT)
public class ForgeClientEventHandler {
    @SubscribeEvent
    public static void clientLogin(PlayerEvent.PlayerLoggedInEvent event) {
        initPB();
    }
}