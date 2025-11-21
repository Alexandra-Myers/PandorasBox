package ivorius.pandorasbox.init;

import ivorius.pandorasbox.item.PandorasBoxItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static ivorius.pandorasbox.PandorasBox.MOD_ID;
import static ivorius.pandorasbox.init.BlockInit.PB;

public class ItemInit {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<PandorasBoxItem> PBI = register("pandoras_box", () -> new PandorasBoxItem(PB.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> INFESTED_END_STONE = register("infested_end_stone", () -> new BlockItem(BlockInit.INFESTED_END_STONE.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> END_STONE_SLAB = register("end_stone_slab", () -> new BlockItem(BlockInit.END_STONE_SLAB.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> END_STONE_STAIRS = register("end_stone_stairs", () -> new BlockItem(BlockInit.END_STONE_STAIRS.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> END_STONE_WALL = register("end_stone_wall", () -> new BlockItem(BlockInit.END_STONE_WALL.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> CHISELED_END_STONE_BRICKS = register("chiseled_end_stone_bricks", () -> new BlockItem(BlockInit.CHISELED_END_STONE_BRICKS.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> INFESTED_CHISELED_END_STONE_BRICKS = register("infested_chiseled_end_stone_bricks", () -> new BlockItem(BlockInit.INFESTED_CHISELED_END_STONE_BRICKS.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> INFESTED_END_STONE_BRICKS = register("infested_end_stone_bricks", () -> new BlockItem(BlockInit.INFESTED_END_STONE_BRICKS.get(), new Item.Properties()));
    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
    public static void registerItems(IEventBus bus) {
        ITEMS.register(bus);
    }
}
