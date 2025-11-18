package ivorius.pandorasbox.init;

import ivorius.pandorasbox.item.PandorasBoxItem;
import net.minecraft.world.item.Item;
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
    private static <T extends Item> RegistryObject<T> register(String name, Supplier<T> item) {
        return ITEMS.register(name, item);
    }
    public static void registerItems(IEventBus bus) {
        ITEMS.register(bus);
    }
}
