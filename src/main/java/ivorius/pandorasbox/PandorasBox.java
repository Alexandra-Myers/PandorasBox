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
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import org.apache.logging.log4j.LogManager;

import java.util.stream.Stream;

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
        PayloadTypeRegistry.playS2C().register(ClientboundUpdateFakeDeathPacket.TYPE, ClientboundUpdateFakeDeathPacket.CODEC);
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
        LootTableEvents.ALL_LOADED.register((resourceManager, registry) -> CONFIG.tables.get().forEach((extra, bases) -> bases.stream().map(registry::getOptional)
                .forEach(optional -> optional.ifPresent(table ->
                        registry.getOptional(extra).ifPresent(extraTable ->
                                table.pools = Stream.concat(table.pools.stream(), extraTable.pools.stream()).toList())))));
    }
    public record ClientboundUpdateFakeDeathPacket() implements CustomPacketPayload {
        public static final Type<ClientboundUpdateFakeDeathPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "fake_death_overlay"));
        public static final StreamCodec<FriendlyByteBuf, ClientboundUpdateFakeDeathPacket> CODEC = CustomPacketPayload.codec(ClientboundUpdateFakeDeathPacket::write, ClientboundUpdateFakeDeathPacket::new);

        public ClientboundUpdateFakeDeathPacket(FriendlyByteBuf buf) {
            this();
        }

        public void write(FriendlyByteBuf buf) {

        }
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}