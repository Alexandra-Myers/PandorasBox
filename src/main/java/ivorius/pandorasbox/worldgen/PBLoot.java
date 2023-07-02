package ivorius.pandorasbox.worldgen;

import ivorius.pandorasbox.init.Registry;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.util.ResourceLocation;

/**
 * Created by lukas on 01.12.14.
 */
public class PBLoot
{
    public static LootTable injectLoot(LootTable table, ResourceLocation location)
    {
        if (location.getNamespace().equals("minecraft"))
        {
            String path = location.getPath();

            table = maybeInject(table, path, "chests/jungle_temple", 5);
            table = maybeInject(table, path, "chests/abandoned_mineshaft", 5);
            table = maybeInject(table, path, "chests/simple_dungeon", 5);
            table = maybeInject(table, path, "chests/desert_pyramid", 5);
            table = maybeInject(table, path, "chests/stronghold_corridor", 5);
            table = maybeInject(table, path, "chests/stronghold_crossing", 5);
            table = maybeInject(table, path, "chests/stronghold_library", 5);
        }
        return table;
    }

    private static LootTable maybeInject(LootTable table, String path, String name, int weight)
    {
        if (path.equals(name))
            return injectIntoLootTable(table, weight);
        return table;
    }

    public static LootTable injectIntoLootTable(LootTable table, int weight)
    {
        table.addPool(LootPool.lootPool().add(ItemLootEntry.lootTableItem(Registry.PBI.get()).setWeight(weight)).build());
        return table;
    }
}
