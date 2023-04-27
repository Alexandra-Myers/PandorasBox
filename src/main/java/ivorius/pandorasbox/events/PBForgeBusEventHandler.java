package ivorius.pandorasbox.events;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.init.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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
