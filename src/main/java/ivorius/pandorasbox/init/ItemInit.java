package ivorius.pandorasbox.init;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.component.PBEffectComponent;
import ivorius.pandorasbox.items.PandorasBoxItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Function;

import static ivorius.pandorasbox.init.BlockInit.PB;

public class ItemInit {
    public static final PandorasBoxItem PBI = register("pandoras_box", (key) -> new PandorasBoxItem(PB, new Item.Properties().component(ComponentInit.EFFECT_COMPONENT, PBEffectComponent.DEFAULT).setId(key)));
    private static <T extends Item> T register(String name, Function<ResourceKey<Item>, T> item) {
        ResourceKey<Item> itemResourceKey = ResourceKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath(PandorasBox.MOD_ID, name));
        return Registry.register(BuiltInRegistries.ITEM, itemResourceKey, item.apply(itemResourceKey));
    }
    public static void registerItems() {

    }
}
