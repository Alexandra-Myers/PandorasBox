/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox;

import ivorius.pandorasbox.commands.PandoraCommand;
import ivorius.pandorasbox.config.PandoraConfig;
import ivorius.pandorasbox.effectholder.EffectHolder;
import ivorius.pandorasbox.effects.PBEffects;
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
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;

import java.util.stream.Stream;

public class PandorasBox implements ModInitializer {
    public static final String MOD_ID = "pandorasbox";
    public static final TagKey<Item> PANDORA_ITEMS = register(Registries.ITEM, "pandoras_box_misc");
    public static final TagKey<Item> PIE_FOODS = registerC(Registries.ITEM, "foods/pie");
    public static final TagKey<Item> BREWING_FUEL = registerVanilla(Registries.ITEM, "brewing_fuel");
    public static final TagKey<Block> OBSIDIANS = registerC(Registries.BLOCK, "obsidians");
    public static final TagKey<Block> NORMAL_OBSIDIANS = registerC(Registries.BLOCK, "obsidians/normal");
    public static final TagKey<Block> CRYING_OBSIDIANS = registerC(Registries.BLOCK, "obsidians/crying");
    public static final TagKey<Block> ALL_TERRACOTTA = register(Registries.BLOCK, "all_terracotta");
    public static PandoraConfig CONFIG;
    public static PrefixLogger logger = new PrefixLogger(LogManager.getLogger());
    public static void initConfig() {
        CONFIG = new PandoraConfig();
    }

    private static <T> TagKey<T> register(ResourceKey<? extends Registry<T>> owner, String tagId) {
        return TagKey.create(owner, ResourceLocation.fromNamespaceAndPath(MOD_ID, tagId));
    }

    private static <T> TagKey<T> registerC(ResourceKey<? extends Registry<T>> owner, String tagId) {
        return TagKey.create(owner, ResourceLocation.fromNamespaceAndPath("c", tagId));
    }

    private static <T> TagKey<T> registerVanilla(ResourceKey<? extends Registry<T>> owner, String tagId) {
        return TagKey.create(owner, ResourceLocation.withDefaultNamespace(tagId));
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
        ServerLifecycleEvents.SERVER_STARTED.register(server -> initPB());
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> initPB());
        CommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext, commandSelection) -> PandoraCommand.register(dispatcher, commandBuildContext));
        LootTableEvents.ALL_LOADED.register((resourceManager, registry) -> CONFIG.tables.get().forEach((extra, bases) -> bases.stream().map(registry::getOptional)
                .forEach(optional -> optional.ifPresent(table ->
                        registry.getOptional(extra).ifPresent(extraTable ->
                                table.pools = Stream.concat(table.pools.stream(), extraTable.pools.stream()).toList())))));
    }
    public static void initPB() {
        PBEffects.registerEffectCreators();
    }
    public record ClientboundUpdateFakeDeathPacket() implements CustomPacketPayload {
        public static final Type<ClientboundUpdateFakeDeathPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, "fake_death_overlay"));
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