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
import net.minecraft.world.item.CreativeModeTabs;
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
        Event<ItemGroupEvents.ModifyEntries> event = ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS);
        event.register(entries -> entries.accept(ItemInit.PBI));
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