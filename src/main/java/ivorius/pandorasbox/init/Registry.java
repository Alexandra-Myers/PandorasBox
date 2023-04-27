package ivorius.pandorasbox.init;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import ivorius.pandorasbox.block.PandorasBoxBlock;
import ivorius.pandorasbox.block.PandorasBoxBlockEntity;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.items.PandorasBoxItem;
import ivorius.pandorasbox.utils.MapExtensions;
import ivorius.pandorasbox.worldgen.WorldGenColorfulTree;
import ivorius.pandorasbox.worldgen.WorldGenLollipop;
import ivorius.pandorasbox.worldgen.WorldGenMegaJungleCustom;
import ivorius.pandorasbox.worldgen.WorldGenRainbow;
import ivorius.pandorasbox.worldgen.structure.SurfaceStructure;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.PlainsVillagePools;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.structure.VillageStructure;
import net.minecraft.world.gen.feature.template.ProcessorLists;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DataSerializerEntry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import static ivorius.pandorasbox.PandorasBox.MOD_ID;

public class Registry {
    private static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MOD_ID);
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MOD_ID);
    private static final DeferredRegister<DataSerializerEntry> SERIALIZERS = DeferredRegister.create(ForgeRegistries.DATA_SERIALIZERS, MOD_ID);
//    private static final DeferredRegister<Structure<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, MOD_ID);

    public static void init(IEventBus bus) {
        FEATURES.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TILES.register(bus);
        ENTITIES.register(bus);
        SERIALIZERS.register(bus);
//        STRUCTURES.register(bus);
    }
    public static final RegistryObject<DataSerializerEntry> PBEFFECTSERIALIZER = SERIALIZERS.register("box_effect", () -> new DataSerializerEntry(PandorasBoxEntity.PBEFFECT_SERIALIZER));
//    public static final RegistryObject<Structure<VillageConfig>> SHRINE_STRUCTURE = STRUCTURES.register("shrine", () -> new SurfaceStructure(VillageConfig.CODEC));
//    public static final JigsawPattern START = JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation("pandorasbox:shrine/start_pool"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.single("pandorasbox:shrine", ProcessorLists.EMPTY), 1), Pair.of(JigsawPiece.single("pandorasbox:shrine_small", ProcessorLists.EMPTY), 100)), JigsawPattern.PlacementBehaviour.RIGID));
    public static final RegistryObject<PandorasBoxBlock> PB = BLOCKS.register("pandoras_box", PandorasBoxBlock::new);
    public static final RegistryObject<PandorasBoxItem> PBI = ITEMS.register("pandoras_box", () -> new PandorasBoxItem(PB.get(), new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<EntityType<PandorasBoxEntity>> Box = ENTITIES.register("pandoras_box", () -> EntityType.Builder.of(PandorasBoxEntity::new, EntityClassification.MISC).fireImmune().noSummon().sized(0.6f, 0.6f).build("pandoras_box"));
    public static final RegistryObject<TileEntityType<PandorasBoxBlockEntity>> TEPB = TILES.register("pandoras_box", () -> TileEntityType.Builder.of(PandorasBoxBlockEntity::new, PB.get()).build(null));
    public static final RegistryObject<Feature<BaseTreeFeatureConfig>> LOLIPOP = FEATURES.register("lolipop", () -> new WorldGenLollipop(BaseTreeFeatureConfig.CODEC, 20));
    public static final RegistryObject<Feature<BaseTreeFeatureConfig>> COLOURFUL_TREE = FEATURES.register("colourful_tree", () -> new WorldGenColorfulTree(BaseTreeFeatureConfig.CODEC, 20));
    public static final RegistryObject<Feature<BaseTreeFeatureConfig>> RAINBOW = FEATURES.register("rainbow", () -> new WorldGenRainbow(BaseTreeFeatureConfig.CODEC, 20));
    public static final RegistryObject<Feature<BaseTreeFeatureConfig>> MEGA_JUNGLE = FEATURES.register("mega_jungle", () -> new WorldGenMegaJungleCustom(BaseTreeFeatureConfig.CODEC, 20));
}
