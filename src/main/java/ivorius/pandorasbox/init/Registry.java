package ivorius.pandorasbox.init;

import ivorius.pandorasbox.block.BlockPandorasBox;
import ivorius.pandorasbox.block.PBBlocks;
import ivorius.pandorasbox.block.TileEntityPandorasBox;
import ivorius.pandorasbox.entitites.EntityPandorasBox;
import ivorius.pandorasbox.items.ItemPandorasBox;
import ivorius.pandorasbox.worldgen.WorldGenColorfulTree;
import ivorius.pandorasbox.worldgen.WorldGenLollipop;
import ivorius.pandorasbox.worldgen.WorldGenMegaJungleCustom;
import ivorius.pandorasbox.worldgen.WorldGenRainbow;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static ivorius.pandorasbox.PandorasBox.MOD_ID;

public class Registry {
    private static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MOD_ID);
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(ForgeRegistries.FEATURES, MOD_ID);
    public static void init(IEventBus bus) {
        FEATURES.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TILES.register(bus);
        ENTITIES.register(bus);

    }
    public static final RegistryObject<BlockPandorasBox> PB = BLOCKS.register("pandoras_box", BlockPandorasBox::new);
    public static final RegistryObject<ItemPandorasBox> PBI = ITEMS.register("pandoras_box", () -> new ItemPandorasBox(PB.get(), new Item.Properties().tab(ItemGroup.TAB_MISC)));
    public static final RegistryObject<EntityType<EntityPandorasBox>> Box = ENTITIES.register("pandoras_box", () -> EntityType.Builder.<EntityPandorasBox>of(EntityPandorasBox::new, EntityClassification.MISC).fireImmune().noSummon().sized(0.6f, 0.6f).build("pandoras_box"));
    public static final RegistryObject<TileEntityType<TileEntityPandorasBox>> TEPB = TILES.register("pandoras_box", () -> TileEntityType.Builder.of(TileEntityPandorasBox::new, PB.get()).build(null));
    public static final RegistryObject<Feature<BaseTreeFeatureConfig>> LOLIPOP = FEATURES.register("lolipop", () -> new WorldGenLollipop(BaseTreeFeatureConfig.CODEC, 20));
    public static final RegistryObject<Feature<BaseTreeFeatureConfig>> COLOURFUL_TREE = FEATURES.register("colourful_tree", () -> new WorldGenColorfulTree(BaseTreeFeatureConfig.CODEC, 20));
    public static final RegistryObject<Feature<BaseTreeFeatureConfig>> RAINBOW = FEATURES.register("rainbow", () -> new WorldGenRainbow(BaseTreeFeatureConfig.CODEC, 20));
    public static final RegistryObject<Feature<BaseTreeFeatureConfig>> MEGA_JUNGLE = FEATURES.register("mega_jungle", () -> new WorldGenMegaJungleCustom(BaseTreeFeatureConfig.CODEC, 20));
}
