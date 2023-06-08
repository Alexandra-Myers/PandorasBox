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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import static ivorius.pandorasbox.PandorasBox.MOD_ID;

public class Registry {
    private static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MOD_ID);
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MOD_ID);
    private static final DeferredRegister<EntityDataSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, MOD_ID);
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
    public static final RegistryObject<EntityDataSerializer<?>> PBEFFECTSERIALIZER = SERIALIZERS.register("box_effect", () -> PandorasBoxEntity.PBEFFECT_SERIALIZER);
//    public static final RegistryObject<Structure<VillageConfig>> SHRINE_STRUCTURE = STRUCTURES.register("shrine", () -> new SurfaceStructure(VillageConfig.CODEC));
//    public static final JigsawPattern START = JigsawPatternRegistry.register(new JigsawPattern(new ResourceLocation("pandorasbox:shrine/start_pool"), new ResourceLocation("empty"), ImmutableList.of(Pair.of(JigsawPiece.single("pandorasbox:shrine", ProcessorLists.EMPTY), 1), Pair.of(JigsawPiece.single("pandorasbox:shrine_small", ProcessorLists.EMPTY), 100)), JigsawPattern.PlacementBehaviour.RIGID));
    public static final RegistryObject<PandorasBoxBlock> PB = BLOCKS.register("pandoras_box", PandorasBoxBlock::new);
    public static final RegistryObject<PandorasBoxItem> PBI = ITEMS.register("pandoras_box", () -> new PandorasBoxItem(PB.get(), new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<EntityType<PandorasBoxEntity>> Box = ENTITIES.register("pandoras_box", () -> EntityType.Builder.of(PandorasBoxEntity::new, EntityClassification.MISC).fireImmune().noSummon().sized(0.6f, 0.6f).build("pandoras_box"));
    public static final RegistryObject<BlockEntityType<PandorasBoxBlockEntity>> TEPB = TILES.register("pandoras_box", () -> BlockEntityType.Builder.of(PandorasBoxBlockEntity::new, PB.get()).build(null));
    public static final RegistryObject<Feature<TreeConfiguration>> LOLIPOP = FEATURES.register("lolipop", () -> new WorldGenLollipop(TreeConfiguration.CODEC, 20));
    public static final RegistryObject<Feature<TreeConfiguration>> COLOURFUL_TREE = FEATURES.register("colourful_tree", () -> new WorldGenColorfulTree(TreeConfiguration.CODEC, 20));
    public static final RegistryObject<Feature<TreeConfiguration>> RAINBOW = FEATURES.register("rainbow", () -> new WorldGenRainbow(TreeConfiguration.CODEC, 20));
    public static final RegistryObject<Feature<TreeConfiguration>> MEGA_JUNGLE = FEATURES.register("mega_jungle", () -> new WorldGenMegaJungleCustom(TreeConfiguration.CODEC, 20));
}
