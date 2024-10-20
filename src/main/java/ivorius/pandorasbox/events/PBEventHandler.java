package ivorius.pandorasbox.events;

import ivorius.pandorasbox.commands.CommandPandorasBox;
import ivorius.pandorasbox.config.AtlasConfig;
import ivorius.pandorasbox.effects.PBEffects;
import ivorius.pandorasbox.networking.AtlasConfigPacket;
import ivorius.pandorasbox.networking.PacketRegistration;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Objects;

import static ivorius.pandorasbox.PandorasBox.*;

/**
 * Created by lukas on 29.07.14.
 */
public class PBEventHandler {
    public void register() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent evt) {
        CommandPandorasBox.register(evt.getDispatcher());
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            logger.info("Sending server config values to client");
            for (AtlasConfig atlasConfig : AtlasConfig.configs.values())
                PacketRegistration.MAIN.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new AtlasConfigPacket(atlasConfig));
        }
    }
    @SubscribeEvent
    public void serverInit(ServerStartedEvent event) {
        AtlasConfig.configs.forEach((resourceLocation, config) -> config.load());
        initPB();
    }
    @SubscribeEvent
    public void datapackReload(OnDatapackSyncEvent event) {
        if(event.getPlayer() != null) return;
        AtlasConfig.configs.forEach((resourceLocation, config) -> {
            config.load();
            PacketRegistration.MAIN.send(PacketDistributor.ALL.noArg(), new AtlasConfigPacket(config));
        });
        initPB();
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
}
