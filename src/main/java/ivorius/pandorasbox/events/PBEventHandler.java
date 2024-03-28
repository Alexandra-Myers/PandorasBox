package ivorius.pandorasbox.events;

import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.commands.CommandPandorasBox;
import ivorius.pandorasbox.effects.PBEffects;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.block.Block;
import net.minecraft.block.SaplingBlock;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.registries.ForgeRegistries;

import static ivorius.pandorasbox.PandorasBox.*;

/**
 * Created by lukas on 29.07.14.
 */
@Mod.EventBusSubscriber(modid = PandorasBox.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PBEventHandler {
    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent evt) {
        CommandPandorasBox.register(evt.getDispatcher());
    }
    @SubscribeEvent
    public void serverInit(FMLServerStartedEvent event) {
        initPB();
    }
    @SubscribeEvent
    public void datapackReload(OnDatapackSyncEvent event) {
        if(event.getPlayer() != null) return;
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
        for (Block block : ForgeRegistries.BLOCKS) {
            if (BlockTags.LOGS.contains(block)) {
                logs.add(block);
            }
            if (BlockTags.LEAVES.contains(block)) {
                leaves.add(block);
            }
            if (BlockTags.SMALL_FLOWERS.contains(block)) {
                flowers.add(block);
            }
            if (BlockTags.WOOL.contains(block)) {
                wool.add(block);
            }
            if (BlockTags.SLABS.contains(block)) {
                slabs.add(block);
            }
            if (BlockTags.STONE_BRICKS.contains(block)) {
                bricks.add(block);
            }
            if (block.getRegistryName().getPath().endsWith("terracotta")) {
                terracotta.add(block);
            }
            if (block.getRegistryName().getPath().endsWith("_terracotta")) {
                stained_terracotta.add(block);
            }
            if (BlockTags.PLANKS.contains(block)) {
                planks.add(block);
            }
            if (block instanceof StainedGlassBlock) {
                stained_glass.add(block);
            }
            if (block instanceof SaplingBlock) {
                saplings.add(block);
            }
            if (BlockTags.FLOWER_POTS.contains(block)) {
                pots.add(block);
            }
        }
        PBEffects.registerEffectCreators();
    }
}
