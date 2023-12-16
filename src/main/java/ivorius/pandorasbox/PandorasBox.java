/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox;

import ivorius.pandorasbox.client.ClientProxy;
import ivorius.pandorasbox.effects.PBEffects;
import ivorius.pandorasbox.events.PBEventHandler;
import ivorius.pandorasbox.init.Registry;
import ivorius.pandorasbox.server.ServerProxy;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@Mod(PandorasBox.MOD_ID)
public class PandorasBox
{
    public static final String NAME = "Pandora's Box";
    public static final String MOD_ID = "pandorasbox";
    public static final String VERSION = "2.3.6-1.20.2";

    public static PandorasBox instance;

    public static PBProxy proxy = runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public static String filePathTexturesFull = "pandorasbox:textures/mod/";
    public static String filePathTextures = "textures/mod/";
    public static String basePath = "pandorasbox:";
    public static Logger logger  = LogManager.getLogger();

    public final IEventBus EVENT_BUS;
    public Feature<TreeConfiguration> LOLIPOP;
    public Feature<TreeConfiguration> COLOURFUL_TREE;
    public Feature<TreeConfiguration> RAINBOW;
    public Feature<TreeConfiguration> MEGA_JUNGLE;
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
    public static PBConfig CONFIG;

    public static PBEventHandler fmlEventHandler;
    public PandorasBox() {
        // Register the setup method for modloading
        initConfig();
        EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        Registry.init(EVENT_BUS);
        EVENT_BUS.addListener(this::preInit);
        instance = this;
    }
    public static void initConfig() {
        CONFIG = new PBConfig();
    }

    public void preInit(final FMLCommonSetupEvent event)
    {
        PBEffects.registerEffects();
        LOLIPOP = Registry.LOLIPOP.get();
        COLOURFUL_TREE = Registry.COLOURFUL_TREE.get();
        RAINBOW = Registry.RAINBOW.get();
        MEGA_JUNGLE = Registry.MEGA_JUNGLE.get();

        fmlEventHandler = new PBEventHandler();
        fmlEventHandler.register();
        proxy.preInit();

        proxy.load();
    }
    public static <T> T runForDist(Supplier<Supplier<T>> clientTarget, Supplier<Supplier<T>> serverTarget) {
        return switch (FMLEnvironment.dist) {
            case CLIENT -> clientTarget.get().get();
            case DEDICATED_SERVER -> serverTarget.get().get();
        };
    }
}