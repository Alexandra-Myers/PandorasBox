/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox;

import ivorius.pandorasbox.commands.PandoraCommand;
import ivorius.pandorasbox.config.PandoraConfig;
import ivorius.pandorasbox.effectcreators.*;
import ivorius.pandorasbox.effectcreators.generate.*;
import ivorius.pandorasbox.effectcreators.generate.block_mappers.*;
import ivorius.pandorasbox.effectholder.EffectHolder;
import ivorius.pandorasbox.entitites.FunctionalGiant;
import ivorius.pandorasbox.init.EntityInit;
import ivorius.pandorasbox.init.Init;
import ivorius.pandorasbox.init.ItemInit;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ZValue;
import net.atlas.atlascore.util.PrefixLogger;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootTable;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.Map;

public class PandorasBox implements ModInitializer {
    public static final String MOD_ID = "pandorasbox";
    public static PandoraConfig CONFIG;
    public static PrefixLogger logger = new PrefixLogger(LogManager.getLogger());
    public static void initConfig() {
        CONFIG = new PandoraConfig();
    }

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        initConfig();
        IValue.bootstrap();
        DValue.bootstrap();
        ZValue.bootstrap();
        EffectHolder.bootstrap();
        Init.init();
        Event<ItemGroupEvents.ModifyEntries> functionalBlocksModifyEvent = ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS);
        functionalBlocksModifyEvent.register(entries -> {
            entries.addAfter(Items.INFESTED_DEEPSLATE, ItemInit.INFESTED_END_STONE, ItemInit.INFESTED_END_STONE_BRICKS, ItemInit.INFESTED_CHISELED_END_STONE_BRICKS);
            entries.accept(ItemInit.PBI);
        });
        Event<ItemGroupEvents.ModifyEntries> buildingBlocksModifyEvent = ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS);
        buildingBlocksModifyEvent.register(entries -> {
            entries.addAfter(Items.END_STONE, ItemInit.END_STONE_STAIRS, ItemInit.END_STONE_SLAB, ItemInit.END_STONE_WALL);
            entries.addAfter(Items.END_STONE_BRICK_WALL, ItemInit.CHISELED_END_STONE_BRICKS);
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> PandorasBoxHelper.initialize());
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> PandorasBoxHelper.initialize());
        CommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext, commandSelection) -> PandoraCommand.register(dispatcher, commandBuildContext));
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            List<ResourceLocation> keysToUse = CONFIG.tables.get().entrySet().stream().filter(entry -> entry.getValue().contains(id)).map(Map.Entry::getKey).toList();
            if (keysToUse.isEmpty())
                return;

            keysToUse.forEach(key -> {
                LootTable table = lootManager.getLootTable(key);
                logger.info("Original Table: " + id.toString() + " Injected Table: " + key);

                if (table != LootTable.EMPTY) {
                    tableBuilder.pools(List.of(table.pools));
                }
            });
        });
        SpawnPlacements.register(EntityInit.GIANT, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FunctionalGiant::checkGiantSpawnRules);
    }
    public record ClientboundUpdateFakeDeathPacket() implements FabricPacket {
        public static final PacketType<ClientboundUpdateFakeDeathPacket> TYPE = PacketType.create(new ResourceLocation(MOD_ID, "fake_death_overlay"), ClientboundUpdateFakeDeathPacket::new);

        public ClientboundUpdateFakeDeathPacket(FriendlyByteBuf buf) {
            this();
        }

        public void write(FriendlyByteBuf buf) {

        }

        @Override
        public PacketType<?> getType() {
            return TYPE;
        }
    }
}