/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox;

import ivorius.pandorasbox.commands.PandoraCommand;
import ivorius.pandorasbox.config.PandoraConfig;
import ivorius.pandorasbox.effectholder.EffectHolder;
import ivorius.pandorasbox.init.EntityInit;
import ivorius.pandorasbox.init.Init;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ZValue;
import ivorius.pandorasbox.utils.EquipmentSet;
import net.atlas.atlascore.util.PrefixLogger;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DataPackRegistryEvent;
import org.apache.logging.log4j.LogManager;

import java.util.List;
import java.util.Map;

@Mod(value = PandorasBox.MOD_ID)
@Mod.EventBusSubscriber(modid = PandorasBox.MOD_ID)
public class PandorasBox {
    public static final String MOD_ID = "pandorasbox";
    public static PandoraConfig CONFIG;
    public static PrefixLogger logger = new PrefixLogger(LogManager.getLogger());
    public static void initConfig() {
        CONFIG = new PandoraConfig();
    }

    public PandorasBox(FMLJavaModLoadingContext context) {
        initConfig();
        IValue.bootstrap();
        DValue.bootstrap();
        ZValue.bootstrap();
        Init.init(context.getModEventBus());
        context.getModEventBus().register(this);
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
                    tableBuilder.pools.addAll(table.pools);
                }
            });
        });
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> PandorasBoxClient::clientInit);
    }

    @SubscribeEvent
    public void onNewDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(Init.EQUIPMENT_SET_REGISTRY_KEY, EquipmentSet.CODEC, EquipmentSet.CODEC);
        event.dataPackRegistry(Init.EFFECT_HOLDER_REGISTRY_KEY, EffectHolder.DIRECT_CODEC, EffectHolder.NETWORK_CODEC);
        event.dataPackRegistry(Init.MELTDOWN_EFFECT_HOLDER_REGISTRY_KEY, EffectHolder.DIRECT_CODEC_NO_TOOLTIP, EffectHolder.NETWORK_CODEC);
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        EffectHolder.bootstrap();
    }

    @SubscribeEvent
    public static void onSpawnPlacement(SpawnPlacementRegisterEvent event) {
        event.register(EntityInit.GIANT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
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