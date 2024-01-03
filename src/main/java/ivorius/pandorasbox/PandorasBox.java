/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox;

import ivorius.pandorasbox.commands.CommandPandorasBox;
import ivorius.pandorasbox.config.AtlasConfig;
import ivorius.pandorasbox.effects.PBEffects;
import ivorius.pandorasbox.init.Init;
import ivorius.pandorasbox.init.ItemInit;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.config.PandoraConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.storage.loot.LootTable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;

public class PandorasBox implements ModInitializer {
    public static final String MOD_ID = "pandorasbox";
    public static Logger logger  = LogManager.getLogger();
    public static ArrayListExtensions<Block> logs;
    public static ArrayListExtensions<Block> leaves;
    public static ArrayListExtensions<Block> flowers;
    public static ArrayListExtensions<Block> terracotta;
    public static ArrayListExtensions<Block> stained_terracotta;
    public static ArrayListExtensions<Block> wool;
    public static ArrayListExtensions<Block> slabs;
    public static ArrayListExtensions<Block> bricks;
    public static ArrayListExtensions<Block> planks;
    public static ArrayListExtensions<Block> stained_glass;
    public static ArrayListExtensions<Block> saplings;
    public static ArrayListExtensions<Block> pots;
    public static PandoraConfig CONFIG;
    public static void initConfig() {
        CONFIG = new PandoraConfig();
    }

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        initConfig();
        Init.init();
        Event<ItemGroupEvents.ModifyEntries> event = ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS);
        event.register(entries -> entries.accept(ItemInit.PBI));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> initPB());
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> initPB());
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> new CommandPandorasBox(dispatcher));
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if (!CONFIG.configuredTables.containsKey(id))
                return;

            LootTable table = lootManager.getLootTable(CONFIG.configuredTables.get(id));

            if (table != LootTable.EMPTY) {
                tableBuilder.pools(table.pools);
            }
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> AtlasConfig.configs.forEach((resourceLocation, config) -> {
            ServerPlayNetworking.send(handler.player, new AtlasConfigPacket(config));
            logger.info("Config packet for config " + resourceLocation.toString() + " sent to client.");
        }));
    }
    public static void initPB() {
        logs = new ArrayListExtensions<>();
        leaves = new ArrayListExtensions<>();
        flowers = new ArrayListExtensions<>();
        wool = new ArrayListExtensions<>();
        slabs = new ArrayListExtensions<>();
        bricks = new ArrayListExtensions<>();
        terracotta = new ArrayListExtensions<>();
        stained_terracotta = new ArrayListExtensions<>();
        planks = new ArrayListExtensions<>();
        stained_glass = new ArrayListExtensions<>();
        saplings = new ArrayListExtensions<>();
        pots = new ArrayListExtensions<>();
        List<Block> blocks = BuiltInRegistries.BLOCK.stream().toList();
        for (Block block : blocks) {
            if (block.defaultBlockState().is(BlockTags.LOGS)) {
                logs.add(block);
            }
            if (block.defaultBlockState().is(BlockTags.LEAVES)) {
                leaves.add(block);
            }
            if (block.defaultBlockState().is(BlockTags.SMALL_FLOWERS)) {
                flowers.add(block);
            }
            if (block.defaultBlockState().is(BlockTags.WOOL)) {
                wool.add(block);
            }
            if (block.defaultBlockState().is(BlockTags.SLABS)) {
                slabs.add(block);
            }
            if (block.defaultBlockState().is(BlockTags.STONE_BRICKS)) {
                bricks.add(block);
            }
            if (Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath().endsWith("terracotta")) {
                terracotta.add(block);
            }
            if (Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath().endsWith("_terracotta")) {
                stained_terracotta.add(block);
            }
            if (block.defaultBlockState().is(BlockTags.PLANKS)) {
                planks.add(block);
            }
            if (block instanceof StainedGlassBlock) {
                stained_glass.add(block);
            }
            if (block instanceof SaplingBlock) {
                saplings.add(block);
            }
            if (block.defaultBlockState().is(BlockTags.FLOWER_POTS)) {
                pots.add(block);
            }
        }
        PBEffects.registerEffectCreators();
    }
    public record AtlasConfigPacket(AtlasConfig config) implements FabricPacket {
        public static final PacketType<AtlasConfigPacket> TYPE = PacketType.create(new ResourceLocation("atlaslib:atlas_config"), AtlasConfigPacket::new);

        public AtlasConfigPacket(FriendlyByteBuf buf) {
            this(AtlasConfig.staticLoadFromNetwork(buf));
        }

        /**
         * Writes the contents of this packet to the buffer.
         *
         * @param buf the output buffer
         */
        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeResourceLocation(config.name);
            config.saveToNetwork(buf);
        }

        /**
         * Returns the packet type of this packet.
         *
         * <p>Implementations should store the packet type instance in a {@code static final}
         * field and return that here, instead of creating a new instance.
         *
         * @return the type of this packet
         */
        @Override
        public PacketType<?> getType() {
            return TYPE;
        }
    }
}