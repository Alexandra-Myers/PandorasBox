/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import ivorius.pandorasbox.commands.PandoraCommand;
import ivorius.pandorasbox.config.PandoraConfig;
import ivorius.pandorasbox.effectcreators.*;
import ivorius.pandorasbox.effectcreators.generate.SimpleConvertEffectCreator;
import ivorius.pandorasbox.effectcreators.generate.block_mappers.BlockMapperCreator;
import ivorius.pandorasbox.effectcreators.generate.block_mappers.CityMapperCreator;
import ivorius.pandorasbox.effectcreators.generate.block_mappers.DirectMapperCreator;
import ivorius.pandorasbox.effectcreators.generate.feature_generators.DirectGeneratorCreator;
import ivorius.pandorasbox.effectcreators.generate.feature_generators.FeatureGeneratorCreator;
import ivorius.pandorasbox.effectholder.EffectHolder;
import ivorius.pandorasbox.effects.generate.block_mappers.CityMapper;
import ivorius.pandorasbox.init.Init;
import ivorius.pandorasbox.init.ItemInit;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ZValue;
import net.atlas.atlascore.AtlasCore;
import net.atlas.atlascore.util.PrefixLogger;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.biome.Biomes;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static ivorius.pandorasbox.effectcreators.PBECConvertToCity.*;

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
        PandorasBoxHelper.preInit();
        Event<ItemGroupEvents.ModifyEntries> event = ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS);
        event.register(entries -> entries.accept(ItemInit.PBI));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            PandorasBoxHelper.initialize();
            Registry<EffectHolder> effectHolders = server.registryAccess().lookupOrThrow(Init.EFFECT_HOLDER_REGISTRY_KEY);
            Registry<EffectHolder> effectHolders1 = server.registryAccess().lookupOrThrow(Init.MELTDOWN_EFFECT_HOLDER_REGISTRY_KEY);
            RegistryAccess access = server.registryAccess();
            for (EffectHolder holder : effectHolders) {
                EffectHolder newHolder;
                if (!(holder.effectCreator instanceof PBECMulti pbecMulti)) {
                    PBEffectCreator creator = manipulateEffectCreator(holder.effectCreator);
                    if (creator == holder.effectCreator) continue;
                    newHolder = holder.wrapEffectCreator(creator);
                } else {
                    List<PBEffectCreator> creators = Arrays.stream(pbecMulti.effects()).map(PandorasBox::manipulateEffectCreator).toList();
                    if (creators.equals(Arrays.asList(pbecMulti.effects()))) continue;
                    newHolder = holder.wrapEffectCreator(new PBECMulti(creators.toArray(PBEffectCreator[]::new), pbecMulti.delays()));
                }
                writeEffectHolderToJSON(effectHolders.getKey(holder), newHolder, "pandora_effect_holders", EffectHolder.DIRECT_CODEC, access);
            }
            for (EffectHolder holder : effectHolders1) {
                EffectHolder newHolder;
                if (!(holder.effectCreator instanceof PBECMulti pbecMulti)) {
                    PBEffectCreator creator = manipulateEffectCreator(holder.effectCreator);
                    if (creator == holder.effectCreator) continue;
                    newHolder = holder.wrapEffectCreator(creator);
                } else {
                    List<PBEffectCreator> creators = Arrays.stream(pbecMulti.effects()).map(PandorasBox::manipulateEffectCreator).toList();
                    if (creators.equals(Arrays.asList(pbecMulti.effects()))) continue;
                    newHolder = holder.wrapEffectCreator(new PBECMulti(creators.toArray(PBEffectCreator[]::new), pbecMulti.delays()));
                }
                writeEffectHolderToJSON(effectHolders1.getKey(holder), newHolder, "meltdown_effect_holders", EffectHolder.DIRECT_CODEC_NO_TOOLTIP, access);
            }
        });
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> PandorasBoxHelper.initialize());
        CommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext, commandSelection) -> PandoraCommand.register(dispatcher, commandBuildContext));
        LootTableEvents.ALL_LOADED.register((resourceManager, registry) -> CONFIG.tables.get().forEach((extra, bases) -> bases.stream().map(registry::getOptional)
                .forEach(optional -> optional.ifPresent(table ->
                        registry.getOptional(extra).ifPresent(extraTable ->
                                table.pools = Stream.concat(table.pools.stream(), extraTable.pools.stream()).toList())))));
    }
    public static PBEffectCreator manipulateEffectCreator(PBEffectCreator effectCreator) {
        if (effectCreator instanceof PBECConvertGeneric pbecConvertGeneric) {
            return new PBECGenerate(pbecConvertGeneric.range(), pbecConvertGeneric.chanceForMoreEffects(), new SimpleConvertEffectCreator(pbecConvertGeneric.simpleConvertEffect().optionalBiome(), pbecConvertGeneric.simpleConvertEffect().excludedTargets(), pbecConvertGeneric.simpleConvertEffect().mappers().stream().map(DirectMapperCreator::new).map(directMapperCreator -> (BlockMapperCreator) directMapperCreator).toList(), pbecConvertGeneric.simpleConvertEffect().generators().stream().map(DirectGeneratorCreator::new).map(directGeneratorCreator -> (FeatureGeneratorCreator) directGeneratorCreator).toList(), pbecConvertGeneric.simpleConvertEffect().spawners()));
        }
        if (effectCreator instanceof PBECConvertToCity pbecConvertToCity) {
            return new PBECGenerate(pbecConvertToCity.range(), 0.1f, new SimpleConvertEffectCreator(Optional.of(Biomes.PLAINS), CITY_MAPPERS.stream().map(blockMapper -> {
                if (blockMapper instanceof CityMapper) return new CityMapperCreator(CITY_TARGETS, pbecConvertToCity.entityIDs(), PandorasBoxHelper.equipmentSets, PandorasBoxHelper.items);
                else return new DirectMapperCreator(blockMapper);
            }).map(mapper -> (BlockMapperCreator) mapper).toList(), Collections.emptyList(), CITY_SPAWNERS));
        }
        return effectCreator;
    }
    public static void writeEffectHolderToJSON(Identifier id, EffectHolder effectHolder, String subLoc, Codec<EffectHolder> codec, RegistryAccess registryAccess) {
        Path dir = Path.of(FabricLoader.getInstance().getGameDir().toAbsolutePath() + "/" + id.getNamespace() + "/pandorasbox/" + subLoc);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            logger.error("Failed to create directories for effect holder!", e);
            return;
        }
        File file = new File(dir.toAbsolutePath() + "/" + id.getPath() + ".json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                logger.error("Failed to create file for effect holder!", e);
                return;
            }
        }
        try (PrintWriter printWriter = new PrintWriter(file)) {
            JsonObject jsonObject = codec.encodeStart(registryAccess.createSerializationContext(JsonOps.INSTANCE), effectHolder).getOrThrow().getAsJsonObject();
            AtlasCore.GSON.toJson(jsonObject, printWriter);
            printWriter.flush();
        } catch (FileNotFoundException e) {
            logger.error("Failed to write file for effect holder!", e);
        }
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