package ivorius.pandorasbox.worldgen;

import ivorius.pandorasbox.block.PBBlocks;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.util.ResourceLocation;

/**
 * Created by lukas on 01.12.14.
 */
public class PBLoot
{
    public static void injectLoot(LootTable table, ResourceLocation location)
    {
        if (location.getNamespace().equals("minecraft"))
        {
            String path = location.getNamespace();

            maybeInject(table, path, "chests/jungle_temple", 5);
            maybeInject(table, path, "chests/abandoned_mineshaft", 5);
            maybeInject(table, path, "chests/simple_dungeon", 5);
            maybeInject(table, path, "chests/desert_pyramid", 5);
            maybeInject(table, path, "chests/stronghold_corridor", 5);
            maybeInject(table, path, "chests/stronghold_crossing", 5);
            maybeInject(table, path, "chests/stronghold_library", 5);
        }
    }

    private static void maybeInject(LootTable table, String path, String name, int weight)
    {
        if (path.equals(name))
            injectIntoLootTable(table, weight);
    }

    public static void injectIntoLootTable(LootTable table, int weight)
    {
        table.addPool(LootPool.lootPool().add(ItemLootEntry.lootTableItem(PBBlocks.pandorasBox).setWeight(weight)).build());
    }
}
