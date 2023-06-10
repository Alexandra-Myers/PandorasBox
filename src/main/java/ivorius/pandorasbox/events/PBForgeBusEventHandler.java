package ivorius.pandorasbox.events;

import ivorius.pandorasbox.PandorasBox;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PandorasBox.MOD_ID)
public class PBForgeBusEventHandler {
//    public static StructureFeature<VillageConfig, ? extends Structure<VillageConfig>> SHRINE;
//    @SubscribeEvent(priority = EventPriority.HIGH)
//    public static void onBiomeLoad(BiomeLoadingEvent event)
//    {
//        if(SHRINE == null) {
//            SHRINE = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, "shrine", SHRINE_STRUCTURE.get().configured(new VillageConfig(() -> START, 7)));
//        }
//        Biome.Category category = event.getCategory();
//        if (category != Biome.Category.NETHER && category != Biome.Category.THEEND)
//        {
//            event.getGeneration().addStructureStart(SHRINE);
//        }
//    }
}
