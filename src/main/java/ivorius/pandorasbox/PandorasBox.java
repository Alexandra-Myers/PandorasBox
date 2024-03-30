/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox;

import ivorius.pandorasbox.client.rendering.PandorasBoxBlockEntityRenderer;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRendererExplosion;
import ivorius.pandorasbox.client.rendering.effects.PBEffectRenderingRegistry;
import ivorius.pandorasbox.effects.PBEffectExplode;
import ivorius.pandorasbox.events.PBEventHandler;
import ivorius.pandorasbox.init.Registry;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.utils.PBEffectArgument;
import net.minecraft.block.Block;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(PandorasBox.MOD_ID)
public class PandorasBox {
    public static final String MOD_ID = "pandorasbox";
    public static PandorasBox instance;
    public static Logger logger  = LogManager.getLogger();
    public final IEventBus EVENT_BUS;
    public Feature<BaseTreeFeatureConfig> LOLIPOP;
    public Feature<BaseTreeFeatureConfig> COLOURFUL_TREE;
    public Feature<BaseTreeFeatureConfig> RAINBOW;
    public Feature<BaseTreeFeatureConfig> MEGA_JUNGLE;
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
    public PandorasBox() {
        // Register the setup method for modloading
        initConfig();
        EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        Registry.init(EVENT_BUS);
        EVENT_BUS.addListener(this::preInit);
        EVENT_BUS.addListener(this::clientInit);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        instance = this;
    }
    public static void initConfig() {
        CONFIG = new PBConfig();
    }


    public void preInit(final FMLCommonSetupEvent event) {
        LOLIPOP = Registry.LOLIPOP.get();
        COLOURFUL_TREE = Registry.COLOURFUL_TREE.get();
        RAINBOW = Registry.RAINBOW.get();
        MEGA_JUNGLE = Registry.MEGA_JUNGLE.get();

        new PBEventHandler().register();

        ArgumentTypes.register("pbeffect", PBEffectArgument.class, new ArgumentSerializer<>(PBEffectArgument::effect));

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> PBEffectRenderingRegistry.registerRenderer(PBEffectExplode.class, new PBEffectRendererExplosion()));
    }
    public void clientInit(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(Registry.Box.get(), PandorasBoxRenderer::new);
        PandorasBoxBlockEntityRenderer.register();
    }
}