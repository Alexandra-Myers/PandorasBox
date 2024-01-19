/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox;

import ivorius.pandorasbox.client.rendering.effects.PBEffectRendererExplosion;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRenderingRegistry;
import ivorius.pandorasbox.config.PandoraConfig;
import ivorius.pandorasbox.effects.PBEffectExplode;
import ivorius.pandorasbox.events.PBEventHandler;
import ivorius.pandorasbox.init.Registry;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

import static net.neoforged.api.distmarker.Dist.CLIENT;

@Mod(PandorasBox.MOD_ID)
public class PandorasBox {
    public static final String MOD_ID = "pandorasbox";
    public static PandorasBox instance;
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
    public static PandoraConfig CONFIG;
    public PandorasBox() {
        // Register the setup method for modloading
        CONFIG = new PandoraConfig();
        EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        Registry.init(EVENT_BUS);
        EVENT_BUS.addListener(this::preInit);
        instance = this;
    }

    public void preInit(final FMLCommonSetupEvent event) {
        LOLIPOP = Registry.LOLIPOP.get();
        COLOURFUL_TREE = Registry.COLOURFUL_TREE.get();
        RAINBOW = Registry.RAINBOW.get();
        MEGA_JUNGLE = Registry.MEGA_JUNGLE.get();

        new PBEventHandler().register();
        runOnClient(() -> () -> PBEffectRenderingRegistry.registerRenderer(PBEffectExplode.class, new PBEffectRendererExplosion()));
    }
    public static void runOnClient(Supplier<Runnable> clientTarget) {
        if (FMLEnvironment.dist == CLIENT)
            clientTarget.get().run();
    }
}