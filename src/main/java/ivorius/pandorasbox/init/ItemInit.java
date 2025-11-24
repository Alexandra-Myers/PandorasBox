package ivorius.pandorasbox.init;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.item.PandorasBoxItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.function.Function;

import static ivorius.pandorasbox.init.BlockInit.PB;

public class ItemInit {
    public static final PandorasBoxItem PBI = register("pandoras_box", (key) -> new PandorasBoxItem(PB, new Item.Properties()));
    public static final BlockItem INFESTED_END_STONE = register("infested_end_stone", (key) -> new BlockItem(BlockInit.INFESTED_END_STONE, new Item.Properties()));
    public static final BlockItem END_STONE_SLAB = register("end_stone_slab", (key) -> new BlockItem(BlockInit.END_STONE_SLAB, new Item.Properties()));
    public static final BlockItem END_STONE_STAIRS = register("end_stone_stairs", (key) -> new BlockItem(BlockInit.END_STONE_STAIRS, new Item.Properties()));
    public static final BlockItem END_STONE_WALL = register("end_stone_wall", (key) -> new BlockItem(BlockInit.END_STONE_WALL, new Item.Properties()));
    public static final BlockItem CHISELED_END_STONE_BRICKS = register("chiseled_end_stone_bricks", (key) -> new BlockItem(BlockInit.CHISELED_END_STONE_BRICKS, new Item.Properties()));
    public static final BlockItem INFESTED_CHISELED_END_STONE_BRICKS = register("infested_chiseled_end_stone_bricks", (key) -> new BlockItem(BlockInit.INFESTED_CHISELED_END_STONE_BRICKS, new Item.Properties()));
    public static final BlockItem INFESTED_END_STONE_BRICKS = register("infested_end_stone_bricks", (key) -> new BlockItem(BlockInit.INFESTED_END_STONE_BRICKS, new Item.Properties()));
    private static <T extends Item> T register(String name, Function<ResourceKey<Item>, T> item) {
        ResourceKey<Item> itemResourceKey = ResourceKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation(PandorasBox.MOD_ID, name));
        return Registry.register(BuiltInRegistries.ITEM, itemResourceKey, item.apply(itemResourceKey));
    }
    public static void registerItems() {

    }
}
