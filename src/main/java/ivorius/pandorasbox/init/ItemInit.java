package ivorius.pandorasbox.init;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.items.PandorasBoxItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import static ivorius.pandorasbox.init.BlockInit.PB;

public class ItemInit {
    public static final PandorasBoxItem PBI = register("pandoras_box", new PandorasBoxItem(PB, new Item.Properties()));
    private static <T extends Item> T register(String name, T item) {
        return Registry.register(BuiltInRegistries.ITEM, ResourceKey.create(BuiltInRegistries.ITEM.key(), new ResourceLocation(PandorasBox.MOD_ID, name)), item);
    }
    public static void registerItems() {

    }
}
