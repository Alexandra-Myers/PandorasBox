package ivorius.pandorasbox.init;

import ivorius.pandorasbox.PandorasBox;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class PandoraItemTags {
    public static final TagKey<Item> PANDORA_ITEMS_MISC_COMMON = register(Registries.ITEM, "pandoras_items_misc_common");
    public static final TagKey<Item> PANDORA_ITEMS_MISC_UNCOMMON = register(Registries.ITEM, "pandoras_items_misc_uncommon");
    public static final TagKey<Item> PANDORA_ITEMS_MISC_RARE = register(Registries.ITEM, "pandoras_items_misc_rare");
    public static final TagKey<Item> PANDORA_ITEMS_MISC_VERY_RARE = register(Registries.ITEM, "pandoras_items_misc_very_rare");
    public static final TagKey<Item> PANDORA_ITEMS_MISC_EPIC = register(Registries.ITEM, "pandoras_items_misc_epic");
    public static final TagKey<Item> PANDORA_ITEMS_DRAGON = register(Registries.ITEM, "pandoras_items_dragon");
    public static final TagKey<Item> PANDORA_ITEMS_LEGENDARY = register(Registries.ITEM, "pandoras_items_legendary");
    public static final TagKey<Item> PANDORA_ITEMS_SINGLES_VERY_RARE = register(Registries.ITEM, "pandoras_items_singles_very_rare");
    public static final TagKey<Item> PANDORA_ITEMS_SINGLES_EPIC = register(Registries.ITEM, "pandoras_items_singles_epic");
    public static final TagKey<Item> PANDORA_ITEMS_EQUIPMENT_COMMON = register(Registries.ITEM, "pandoras_items_equipment_common");
    public static final TagKey<Item> PANDORA_ITEMS_EQUIPMENT_UNCOMMON = register(Registries.ITEM, "pandoras_items_equipment_uncommon");
    public static final TagKey<Item> PANDORA_ITEMS_EQUIPMENT_RARE = register(Registries.ITEM, "pandoras_items_equipment_rare");
    public static final TagKey<Item> PANDORA_ITEMS_EQUIPMENT_VERY_RARE = register(Registries.ITEM, "pandoras_items_equipment_very_rare");

    public static <T> TagKey<T> register(ResourceKey<? extends Registry<T>> owner, String tagId) {
        return TagKey.create(owner, Identifier.fromNamespaceAndPath(PandorasBox.MOD_ID, tagId));
    }
}
