/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox;

import com.mojang.brigadier.arguments.ArgumentType;
import ivorius.pandorasbox.block.PandorasBoxBlockEntity;
import ivorius.pandorasbox.client.ClientProxy;
import ivorius.pandorasbox.client.PBSpriteSourceProvider;
import ivorius.pandorasbox.client.rendering.PandorasBoxBlockEntityRenderer;
import ivorius.pandorasbox.client.rendering.PandorasBoxRenderer;
import ivorius.pandorasbox.effects.PBEffects;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.events.PBEventHandler;
import ivorius.pandorasbox.init.Registry;
import ivorius.pandorasbox.server.ServerProxy;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.utils.PBEffectArgument;
import ivorius.pandorasbox.weighted.WeightedSelector;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

import static net.minecraft.core.registries.BuiltInRegistries.COMMAND_ARGUMENT_TYPE;
import static net.minecraftforge.registries.ForgeRegistries.COMMAND_ARGUMENT_TYPES;

@Mod(PandorasBox.MOD_ID)
public class PandorasBox
{
    public static final String NAME = "Pandora's Box";
    public static final String MOD_ID = "pandorasbox";
    public static final String VERSION = "2.2.1-1.16.5";

    public static PandorasBox instance;

    public static PBProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);

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

    public static PBEventHandler fmlEventHandler;
    public PandorasBox() {
        // Register the setup method for modloading
        EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        Registry.init(EVENT_BUS);
        EVENT_BUS.addListener(this::preInit);
        EVENT_BUS.addListener(this::gatherData);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        instance = this;
    }

    public void preInit(final FMLCommonSetupEvent event)
    {
        PBEffects.registerEffects();
        LOLIPOP = Registry.LOLIPOP.get();
        COLOURFUL_TREE = Registry.COLOURFUL_TREE.get();
        RAINBOW = Registry.RAINBOW.get();
        MEGA_JUNGLE = Registry.MEGA_JUNGLE.get();
//        if(SHRINE == null) {
//            SHRINE = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, "shrine", SHRINE_STRUCTURE.get().configured(new VillageConfig(() -> START, 7)));
//        }
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, PBConfig.commonSpec);
        PBConfig.loadConfig();

        fmlEventHandler = new PBEventHandler();
        fmlEventHandler.register();

        ArgumentTypeInfos.register(COMMAND_ARGUMENT_TYPE, "pbeffect", PBEffectArgument.class, SingletonArgumentInfo.contextFree(PBEffectArgument::effect));

        proxy.preInit();

        proxy.load();
    }
    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();

        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        gen.addProvider(event.includeClient(), new PBSpriteSourceProvider(packOutput, existingFileHelper));
    }
}