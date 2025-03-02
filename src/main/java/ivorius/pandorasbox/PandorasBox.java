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
import net.atlas.atlascore.util.ArrayListExtensions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class PandorasBox implements ModInitializer {
    public static final String MOD_ID = "pandorasbox";
    public static final TagKey<Item> PANDORA_ITEMS = register("pandoras_box_misc");
    public static ArrayListExtensions<Block> logs = new ArrayListExtensions<>();
    public static ArrayListExtensions<Block> leaves = new ArrayListExtensions<>();
    public static ArrayListExtensions<Block> flowers = new ArrayListExtensions<>();
    public static ArrayListExtensions<Block> terracotta = new ArrayListExtensions<>();
    public static ArrayListExtensions<Block> stained_terracotta = new ArrayListExtensions<>();
    public static ArrayListExtensions<Block> wool = new ArrayListExtensions<>();
    public static ArrayListExtensions<Block> slabs = new ArrayListExtensions<>();
    public static ArrayListExtensions<Block> stairs = new ArrayListExtensions<>();
    public static ArrayListExtensions<Block> bricks = new ArrayListExtensions<>();
    public static ArrayListExtensions<Block> planks = new ArrayListExtensions<>();
    public static ArrayListExtensions<Block> stained_glass = new ArrayListExtensions<>();
    public static ArrayListExtensions<Block> saplings = new ArrayListExtensions<>();
    public static ArrayListExtensions<Block> pots = new ArrayListExtensions<>();
    public static PandoraConfig CONFIG;
    public static void initConfig() {
        CONFIG = new PandoraConfig();
    }

    private static TagKey<Item> register(String tagId) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MOD_ID, tagId));
    }

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
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
        logs.clear();
        leaves.clear();
        flowers.clear();
        wool.clear();
        slabs.clear();
        stairs.clear();
        bricks.clear();
        terracotta.clear();
        stained_terracotta.clear();
        planks.clear();
        stained_glass.clear();
        saplings.clear();
        pots.clear();
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
            if (block.defaultBlockState().is(BlockTags.STAIRS)) {
                stairs.add(block);
            }
            if (block.defaultBlockState().is(BlockTags.STONE_BRICKS)) {
                bricks.add(block);
            }
            if (Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath().endsWith("terracotta")) {
                terracotta.add(block);
            }
            if (block.defaultBlockState().is(ConventionalBlockTags.GLAZED_TERRACOTTAS) || (block.defaultBlockState().is(BlockTags.TERRACOTTA) && !block.defaultBlockState().is(Blocks.TERRACOTTA))) {
                stained_terracotta.add(block);
            }
            if (block.defaultBlockState().is(BlockTags.PLANKS)) {
                planks.add(block);
            }
            if (block.defaultBlockState().is(ConventionalBlockTags.GLASS_BLOCKS) || block.defaultBlockState().is(ConventionalBlockTags.GLASS_PANES)) {
                stained_glass.add(block);
            }
            if (block.defaultBlockState().is(BlockTags.SAPLINGS)) {
                saplings.add(block);
            }
            if (block.defaultBlockState().is(BlockTags.FLOWER_POTS)) {
                pots.add(block);
            }
        }
        PBEffects.registerEffectCreators();
    }
}