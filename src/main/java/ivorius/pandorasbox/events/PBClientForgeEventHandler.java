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

@Mod.EventBusSubscriber(modid = PandorasBox.MOD_ID, value = Dist.CLIENT)
public class PBClientForgeEventHandler {
    @SubscribeEvent
    public static void clientTest(ClientPlayerNetworkEvent.LoggingIn networkEvent) {
        for (Map.Entry<ResourceKey<EntityDataSerializer<?>>, EntityDataSerializer<?>> entry : NeoForgeRegistries.ENTITY_DATA_SERIALIZERS.entrySet()) {
            int id = NeoForgeRegistries.ENTITY_DATA_SERIALIZERS.getId(entry.getValue());
            ResourceLocation rl = NeoForgeRegistries.ENTITY_DATA_SERIALIZERS.getKey(entry.getValue());
            logger.info("Resource Location: " + rl.toString() + " Id: " + id);
        }
    }
}
