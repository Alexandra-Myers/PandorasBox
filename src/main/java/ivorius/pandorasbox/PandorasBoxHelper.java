/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import ivorius.pandorasbox.random.ILinear;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import ivorius.pandorasbox.utils.RandomizedItemTag;
import ivorius.pandorasbox.utils.WeightedWithRandomCount;
import ivorius.pandorasbox.weighted.*;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.TeleportRandomlyConsumeEffect;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class PandorasBoxHelper {
    public static final List<Property<?>> validProperties = List.of(BlockStateProperties.HALF, BlockStateProperties.RAIL_SHAPE, BlockStateProperties.LEVEL_HONEY, BlockStateProperties.SLAB_TYPE, BlockStateProperties.WATERLOGGED,
            BlockStateProperties.BAMBOO_LEAVES, BlockStateProperties.NOTEBLOCK_INSTRUMENT, BlockStateProperties.FACING, BlockStateProperties.AXIS, BlockStateProperties.LIT,
            BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.STAIRS_SHAPE);
    public static Object2ObjectLinkedOpenHashMap<EitherArrayList<WeightedBlock, WeightedTag<Block>>, Collection<WeightedBlock>> cachedBlockLists = new Object2ObjectLinkedOpenHashMap<>();
    public static Object2ObjectLinkedOpenHashMap<EitherArrayList<RandomizedItemStack, RandomizedItemTag>, List<RandomizedItemStack>> cachedRandomizedStackLists = new Object2ObjectLinkedOpenHashMap<>();
    public static List<WeightedEntity> landMobs = new ArrayList<>();
    public static List<WeightedEntity> mobs = new ArrayList<>();
    public static List<WeightedEntity> creatures = new ArrayList<>();
    public static List<WeightedEntity> waterCreatures = new ArrayList<>();
    public static List<WeightedEntity> waterMobs = new ArrayList<>();
    public static List<WeightedEntity> tameableCreatures = new ArrayList<>();

    public static EitherArrayList<RandomizedItemStack, RandomizedItemTag> blocksAndItems = new EitherArrayList<>();
    public static Multimap<Block, Property<?>> randomizableBlockProperties = HashMultimap.create();

    public static EitherArrayList<WeightedBlock, WeightedTag<Block>> blocks = new EitherArrayList<>();

    public static EitherArrayList<RandomizedItemStack, RandomizedItemTag> items = new EitherArrayList<>();
    public static List<WeightedSet> equipmentSets = new ArrayList<>();
    public static Hashtable<Item, Hashtable<Integer, ItemStack>> equipmentForLevels = new Hashtable<>();

    public static List<WeightedPotion> buffs = new ArrayList<>();
    public static List<WeightedPotion> debuffs = new ArrayList<>();

    public static EitherArrayList<RandomizedItemStack, RandomizedItemTag> enchantableArmorList = new EitherArrayList<>();
    public static EitherArrayList<RandomizedItemStack, RandomizedItemTag> enchantableToolList = new EitherArrayList<>();

    public static EitherArrayList<WeightedBlock, WeightedTag<Block>> heavyBlocks = new EitherArrayList<>();

    public static void addEntities(List<WeightedEntity> list, double weight, int minNumber, int maxNumber, String... entities) {
        for (String s : entities) {
            list.add(new WeightedEntity(weight, s, minNumber, maxNumber));
        }
    }

    @SafeVarargs
    public static void addBlockTags(double weight, TagKey<Block>... blocks) {
        for (TagKey<Block> blockTagKey : blocks) {
            PandorasBoxHelper.blocks.add(Either.right(new WeightedTag<>(weight, blockTagKey)));

            blockTagKey.cast(Registries.ITEM).ifPresent(itemTag -> blocksAndItems.add(Either.right(new RandomizedItemTag(itemTag, new WeightedWithRandomCount(1, Optional.empty(), weight)))));
        }
    }

    public static void addBlocks(double weight, Block... blocks) {
        for (Block block : blocks) {
            PandorasBoxHelper.blocks.add(Either.left(new WeightedBlock(weight, block)));

            Item item = block.asItem();
            blocksAndItems.add(Either.left(new RandomizedItemStack(item, 1, item.getDefaultMaxStackSize(), weight)));
        }
    }
    public static void addBlocks(double weight, List<Block> blocks) {
        for (Block block : blocks) {
            PandorasBoxHelper.blocks.add(Either.left(new WeightedBlock(weight, block)));

            Item item = block.asItem();
            blocksAndItems.add(Either.left(new RandomizedItemStack(item, 1, item.getDefaultMaxStackSize(), weight)));
        }
    }

    public static void addBlocks(EitherArrayList<WeightedBlock, WeightedTag<Block>> list, double weight, Block... blocks) {
        for (Block block : blocks) {
            list.add(Either.left(new WeightedBlock(weight, block)));
        }
    }

    public static void addItem(RandomizedItemStack randomizedItemStack) {
        items.add(Either.left(randomizedItemStack));
        blocksAndItems.add(Either.left(randomizedItemStack));
    }

    public static void addTag(RandomizedItemTag randomizedItemTag) {
        items.add(Either.right(randomizedItemTag));
        blocksAndItems.add(Either.right(randomizedItemTag));
    }

    @SafeVarargs
    public static void addTags(double weight, TagKey<Item>... tags) {
        for (TagKey<Item> tagKey : tags) {
            addTag(new RandomizedItemTag(tagKey, new WeightedWithRandomCount(1, Optional.empty(), weight)));
        }
    }

    @SafeVarargs
    public static void addTagsMinMax(double weight, int min, int max, TagKey<Item>... tags) {
        for (TagKey<Item> tagKey : tags) {
            addTag(new RandomizedItemTag(tagKey, new WeightedWithRandomCount(min, max, weight)));
        }
    }

    public static void addItems(double weight, Object... items) {
        for (Object object : items) {
            if (object instanceof Item item) {
                addItem(new RandomizedItemStack(item, 1, item.getDefaultMaxStackSize(), weight));
            } else if (object instanceof ItemStack itemStack) {
                addItem(new RandomizedItemStack(itemStack, new WeightedWithRandomCount(1, itemStack.getMaxStackSize(), weight)));
            }
        }
    }

    public static void addItemsMinMax(double weight, int min, int max, Object... items) {
        for (Object object : items) {
            if (object instanceof Item item) {
                addItem(new RandomizedItemStack(item, min, max, weight));
            } else if (object instanceof ItemStack itemStack) {
                addItem(new RandomizedItemStack(itemStack, new WeightedWithRandomCount(min, max, weight)));
            }
        }
    }

    public static void addEquipmentSet(double weight, Object... items) {
        ItemStack[] set = new ItemStack[items.length];

        for (int i = 0; i < set.length; i++) {
            if (items[i] instanceof Item item) {
                set[i] = new ItemStack(item);
            } else if (items[i] instanceof ItemStack itemStack) {
                set[i] = itemStack;
            }
        }

        equipmentSets.add(new WeightedSet(weight, set));
    }

    @SafeVarargs
    public static void addPotions(List<WeightedPotion> list, double weight, int minAmplifier, int maxAmplifier, int minDuration, int maxDuration, Holder<MobEffect>... potions) {
        for (Holder<MobEffect> effect : potions) {
            list.add(new WeightedPotion(weight, HolderSet.direct(effect), new ILinear(minAmplifier, maxAmplifier), new ILinear(minDuration, maxDuration)));
        }
    }

    @SafeVarargs
    public static void addPotions(List<WeightedPotion> list, double weight, int minAmplifier, int maxAmplifier, int minDuration, int maxDuration, HolderSet<MobEffect>... potions) {
        for (HolderSet<MobEffect> effect : potions) {
            list.add(new WeightedPotion(weight, effect, new ILinear(minAmplifier, maxAmplifier), new ILinear(minDuration, maxDuration)));
        }
    }

    public static void addEnchantableArmor(double weight, Object... items) {
        for (Object object : items) {
            if (object instanceof Item item) {
                enchantableArmorList.add(Either.left(new RandomizedItemStack(item, 1, 1, weight)));
            } else if (object instanceof ItemStack itemStack) {
                enchantableArmorList.add(Either.left(new RandomizedItemStack(itemStack, new WeightedWithRandomCount(1, 1, weight))));
            }
        }
    }

    public static void addEnchantableTools(double weight, Object... items) {
        for (Object object : items) {
            if (object instanceof Item item) {
                enchantableToolList.add(Either.left(new RandomizedItemStack(item, 1, 1, weight)));
            } else if (object instanceof ItemStack itemStack) {
                enchantableToolList.add(Either.left(new RandomizedItemStack(itemStack, new WeightedWithRandomCount(1, 1, weight))));
            }
        }
    }

    public static void addEquipmentForLevel(Item base, int level, ItemStack stack) {
        if (!equipmentForLevels.containsKey(base))
            equipmentForLevels.put(base, new Hashtable<>());

        equipmentForLevels.get(base).put(level, stack);
    }

    public static void addEquipmentLevelsInOrder(Item base, Object... items) {
        for (int i = 0; i < items.length; i++) {
            Object object = items[i];

            if (object instanceof Item)
                addEquipmentForLevel(base, i, new ItemStack((Item) items[i]));
            else if (object instanceof ItemStack)
                addEquipmentForLevel(base, i, (ItemStack) items[i]);
        }
    }

    public static void addAllRandomizableBlockProperties() {
        for (Block block : BuiltInRegistries.BLOCK) randomizableBlockProperties.putAll(block, block.defaultBlockState().getProperties().stream().filter(validProperties::contains).toList());
    }

    public static void initialize() {
        cachedBlockLists.clear();
        cachedRandomizedStackLists.clear();
        landMobs.clear();
        mobs.clear();
        creatures.clear();
        waterCreatures.clear();
        waterMobs.clear();
        tameableCreatures.clear();
        blocks.clear();
        randomizableBlockProperties.clear();
        blocksAndItems.clear();
        items.clear();
        equipmentSets.clear();
        equipmentForLevels.clear();
        buffs.clear();
        debuffs.clear();
        enchantableArmorList.clear();
        enchantableToolList.clear();
        heavyBlocks.clear();
        addEntities(landMobs, 10.0, 3, 10, "zombie", "drowned");
        addEntities(landMobs, 7.5, 3, 10, "husk");
        addEntities(landMobs, 10.0, 2, 8, "spider");
        addEntities(landMobs, 10.0, 2, 5, "skeleton");
        addEntities(landMobs, 10.0, 2, 5, "pillager");
        addEntities(landMobs, 7.5, 2, 5, "stray");
        addEntities(landMobs, 5.0, 2, 5, "wither_skeleton");
        addEntities(landMobs, 10.0, 2, 8, "creeper");
        addEntities(landMobs, 6.0, 2, 8, "slime");
        addEntities(landMobs, 6.0, 2, 8, "zombified_piglin");
        addEntities(landMobs, 6.0, 2, 8, "hoglin");
        addEntities(landMobs, 6.0, 2, 6, "enderman");
        addEntities(landMobs, 5.0, 2, 4, "cave_spider");
        addEntities(landMobs, 5.0, 10, 20, "silverfish");
        addEntities(landMobs, 5.0, 2, 6, "magma_cube");
        addEntities(landMobs, 4.0, 2, 8, "vindicator");
        addEntities(landMobs, 4.0, 2, 4, "zoglin");
        addEntities(landMobs, 4.0, 2, 4, "witch");
        addEntities(landMobs, 4.0, 10, 20, "endermite");
        addEntities(landMobs, 5.0, 2, 6, "pbspecial_angry_wolf");
        addEntities(landMobs, 4.0, 2, 5, "pbspecial_charged_creeper");
        addEntities(landMobs, 4.0, 2, 5, "breeze");
        addEntities(landMobs, 3.0, 1, 1, "evoker");
        addEntities(landMobs, 2.0, 2, 3, "piglin_brute");
        addEntities(landMobs, 1.0, 1, 1, "ravager");
        mobs.addAll(landMobs);
        addEntities(mobs, 1.0, 1, 1, "wither");
        addEntities(mobs, 4.0, 2, 5, "blaze");
        addEntities(mobs, 4.0, 1, 4, "ghast");
        addEntities(mobs, 4.0, 1, 4, "phantom");

        addEntities(creatures, 10.0, 3, 10, "pig", "sheep", "cow", "chicken");
        addEntities(creatures, 6.0, 2, 6, "wolf");
        addEntities(creatures, 6.0, 2, 6, "panda");
        addEntities(creatures, 6.0, 2, 2, "polar_bear");
        addEntities(creatures, 6.0, 2, 2, "sniffer");
        addEntities(creatures, 6.0, 2, 6, "fox");
        addEntities(creatures, 6.0, 2, 6, "allay");
        addEntities(creatures, 5.0, 4, 10, "bat");
        addEntities(creatures, 5.0, 4, 10, "bee");
        addEntities(creatures, 6.0, 4, 10, "frog");
        addEntities(creatures, 7.0, 6, 20, "rabbit");
        addEntities(creatures, 4.0, 3, 7, "mooshroom");
        addEntities(creatures, 4.0, 3, 7, "snow_golem");
        addEntities(creatures, 4.0, 2, 5, "horse", "donkey", "mule");
        addEntities(creatures, 4.0, 2, 5, "llama");
        addEntities(creatures, 4.0, 2, 6, "ocelot", "cat");
        addEntities(creatures, 4.0, 2, 6, "parrot");
        addEntities(creatures, 3.0, 3, 6, "wandering_trader");
        addEntities(creatures, 3.0, 3, 6, "villager");
        addEntities(creatures, 3.0, 3, 6, "goat");
        addEntities(creatures, 3.0, 3, 6, "piglin");
        addEntities(creatures, 3.0, 2, 4, "iron_golem");

        addEntities(waterCreatures, 6.0, 6, 20, "squid", "glow_squid", "dolphin", "cod", "salmon", "pufferfish", "turtle", "tropical_fish", "axolotl", "tadpole");

        addEntities(waterMobs, 6.0, 3, 10, "drowned");
        addEntities(waterMobs, 6.0, 3, 10, "guardian");
        addEntities(waterMobs, 5.0, 1, 1, "elder_guardian");

        addEntities(tameableCreatures, 4.0, 1, 4, "pbspecial_wolf_tamed");
        addEntities(tameableCreatures, 4.0, 1, 4, "pbspecial_cat_tamed");
        addEntities(tameableCreatures, 4.0, 1, 4, "pbspecial_parrot_tamed");

        addBlockTags(40.0, BlockTags.PLANKS, BlockTags.WOOL, BlockTags.LEAVES, BlockTags.LOGS, BlockTags.SLABS, BlockTags.STAIRS, BlockTags.STONE_BRICKS);
        addBlocks(15.0, Blocks.PRISMARINE, Blocks.QUARTZ_BLOCK, Blocks.SMOOTH_QUARTZ);
        addBlocks(10.0, Blocks.GRAVEL, Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.CLAY, Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_TILES, Blocks.NETHER_BRICKS, Blocks.BRICKS, Blocks.END_STONE, Blocks.END_STONE_BRICKS);
        addBlockTags(10.0, ConventionalBlockTags.COBBLESTONES, BlockTags.NYLIUM, PandorasBox.ALL_TERRACOTTA, ConventionalBlockTags.PLAYER_WORKSTATIONS_CRAFTING_TABLES, ConventionalBlockTags.PLAYER_WORKSTATIONS_FURNACES, BlockTags.BASE_STONE_NETHER, BlockTags.DIRT);
        addBlockTags(8.0, BlockTags.SAND, ConventionalBlockTags.STONES, BlockTags.WITHER_SUMMON_BASE_BLOCKS, ConventionalBlockTags.QUARTZ_ORES, BlockTags.COAL_ORES, BlockTags.COPPER_ORES, BlockTags.LAPIS_ORES, BlockTags.REDSTONE_ORES, BlockTags.SNOW, ConventionalBlockTags.CHESTS, ConventionalBlockTags.BARRELS, ConventionalBlockTags.SANDSTONE_BLOCKS, ConventionalBlockTags.VILLAGER_JOB_SITES, BlockTags.RAILS, ConventionalBlockTags.CONCRETES, BlockTags.CONCRETE_POWDER, BlockTags.SAPLINGS, BlockTags.FLOWER_POTS, ConventionalBlockTags.GLASS_BLOCKS, ConventionalBlockTags.GLASS_PANES);
        addBlocks(0.2, Blocks.LODESTONE);
        addBlockTags(0.2, ConventionalBlockTags.STORAGE_BLOCKS_NETHERITE, ConventionalBlockTags.STORAGE_BLOCKS_DIAMOND, ConventionalBlockTags.STORAGE_BLOCKS_EMERALD, ConventionalBlockTags.STORAGE_BLOCKS_GOLD);
        addBlocks(0.3, Blocks.IRON_BLOCK);
        addBlockTags(0.5, ConventionalBlockTags.NETHERITE_SCRAP_ORES, BlockTags.DIAMOND_ORES, BlockTags.EMERALD_ORES, BlockTags.GOLD_ORES);
        addBlockTags(1.0, BlockTags.IRON_ORES);
        addBlocks(2.0, Blocks.TNT, Blocks.GLOWSTONE, Blocks.SHROOMLIGHT, Blocks.SPONGE);
        addBlockTags(2.0, ConventionalBlockTags.STORAGE_BLOCKS_COAL, ConventionalBlockTags.STORAGE_BLOCKS_COPPER, ConventionalBlockTags.STORAGE_BLOCKS_LAPIS, ConventionalBlockTags.STORAGE_BLOCKS_REDSTONE, ConventionalBlockTags.STORAGE_BLOCKS_SLIME, ConventionalBlockTags.STORAGE_BLOCKS_RESIN);
        addBlocks(5.0, Blocks.DRAGON_EGG, Blocks.NOTE_BLOCK, Blocks.REDSTONE_LAMP, Blocks.SEA_LANTERN, Blocks.SNOW, Blocks.BOOKSHELF, Blocks.JACK_O_LANTERN, Blocks.MELON, Blocks.CHISELED_BOOKSHELF);
        addBlockTags(5.0, ConventionalBlockTags.STORAGE_BLOCKS_WHEAT, ConventionalBlockTags.STORAGE_BLOCKS_DRIED_KELP, ConventionalBlockTags.NORMAL_OBSIDIANS, ConventionalBlockTags.CRYING_OBSIDIANS);

        addItems(10.0, Items.CLAY_BALL, Items.FISHING_ROD, Items.FLINT, Items.EGG, Items.PAPER, Items.TORCH, Items.SOUL_TORCH);
        addTags(10.0, ItemTags.COALS, ConventionalItemTags.COPPER_INGOTS,
                ConventionalItemTags.CROPS, ConventionalItemTags.FERTILIZERS,
                ConventionalItemTags.EMPTY_BUCKETS, ConventionalItemTags.REDSTONE_DUSTS,
                ConventionalItemTags.WOODEN_RODS, ItemTags.VILLAGER_PLANTABLE_SEEDS,
                ConventionalItemTags.BRICKS, PandorasBox.PANDORA_ITEMS,
                ConventionalItemTags.GOLD_NUGGETS, ConventionalItemTags.FISHING_ROD_TOOLS,
                ConventionalItemTags.RAW_MEAT_FOODS, ConventionalItemTags.RAW_FISH_FOODS,
                ConventionalItemTags.COOKED_MEAT_FOODS, ConventionalItemTags.COOKED_FISH_FOODS,
                ConventionalItemTags.BREAD_FOODS, ConventionalItemTags.COOKIE_FOODS,
                ConventionalItemTags.BERRY_FOODS, ConventionalItemTags.PIE_FOODS,
                ConventionalItemTags.FOOD_POISONING_FOODS, ConventionalItemTags.CANDY_FOODS,
                ConventionalItemTags.EDIBLE_WHEN_PLACED_FOODS);
        addItems(10.0, Items.HONEYCOMB, Items.MUSHROOM_STEW, Items.APPLE, Items.RABBIT_FOOT, Items.RABBIT_HIDE, Items.RABBIT_STEW, Items.HONEY_BOTTLE);
        addItems(8.0,  Items.PAINTING, Items.FLOWER_POT, Items.MINECART, Items.CAULDRON);
        addTags(8.0, ItemTags.BOATS, ItemTags.BEDS, ConventionalItemTags.LEATHERS,
                ConventionalItemTags.MILK_BUCKETS, ConventionalItemTags.LAVA_BUCKETS, ConventionalItemTags.WATER_BUCKETS,
                ConventionalItemTags.BRUSH_TOOLS, ConventionalItemTags.IGNITER_TOOLS, ConventionalItemTags.SOUP_FOODS,
                ConventionalItemTags.SLIME_BALLS);
        addItems(8.0, Items.NAME_TAG, Items.NAUTILUS_SHELL, Items.INK_SAC, Items.GLOW_INK_SAC, Items.ARMADILLO_SCUTE, Items.LANTERN, Items.SOUL_LANTERN, Items.SPYGLASS);
        addTags(6.0, ConventionalItemTags.IRON_INGOTS, ConventionalItemTags.IRON_NUGGETS, ConventionalItemTags.GLOWSTONE_DUSTS,
                ConventionalItemTags.AMETHYST_GEMS, ConventionalItemTags.RODS, ItemTags.BREWING_FUEL);
        addItems(6.0, Items.WIND_CHARGE, Items.CLOCK, Items.GHAST_TEAR, Items.ENDER_EYE, Items.GLISTERING_MELON_SLICE, Items.FERMENTED_SPIDER_EYE, Items.MAGMA_CREAM, Items.GOLDEN_CARROT, Items.TURTLE_SCUTE, Items.PHANTOM_MEMBRANE);
        addItems(4.0, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.WOODEN_SWORD, Items.WOODEN_PICKAXE, Items.WOODEN_SHOVEL, Items.WOODEN_AXE, Items.WOODEN_HOE);
        addItems(4.0, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_SWORD, Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE);
        addItems(4.0, Items.TURTLE_HELMET, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_SWORD, Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE);
        addItems(4.0, Items.COMPASS, Items.LEAD, Items.CHORUS_FRUIT, Items.HEART_OF_THE_SEA);
        addItems(3.0, Items.SHIELD, Items.WOLF_ARMOR, Items.LEATHER_HORSE_ARMOR, Items.IRON_HORSE_ARMOR, Items.GOLDEN_HORSE_ARMOR);
        addTagsMinMax(5.0, 1, 1, ConventionalItemTags.CHESTS, ConventionalItemTags.BARRELS);
        addTagsMinMax(2.0, 1, 1, ItemTags.ANVIL);
        addItemsMinMax(2.0, 1, 1, Items.NETHER_STAR, Items.BEACON, Items.DISPENSER, Items.JUKEBOX, Items.ENCHANTING_TABLE);
        addTags(2.0, ConventionalItemTags.MUSIC_DISCS, ConventionalItemTags.DIAMOND_GEMS, ConventionalItemTags.EMERALD_GEMS, ConventionalItemTags.GOLD_INGOTS, ConventionalItemTags.GOLDEN_FOODS, ConventionalItemTags.ENDER_PEARLS, ConventionalItemTags.PRISMARINE_GEMS);
        addItems(2.0, Items.PRISMARINE_SHARD, Items.OMINOUS_BOTTLE);
        addItemsMinMax(0.5, 1, 5, Items.NETHERITE_SCRAP, Items.DISC_FRAGMENT_5, Items.ECHO_SHARD, Items.RECOVERY_COMPASS);
        addItems(1.0, Items.ELYTRA, Items.DRAGON_BREATH);
        addItems(2.0, Items.DIAMOND_HORSE_ARMOR, Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS, Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE, Items.DIAMOND_SHOVEL, Items.DIAMOND_AXE, Items.DIAMOND_HOE);
        addItems(0.2, Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS, Items.NETHERITE_SWORD, Items.NETHERITE_PICKAXE, Items.NETHERITE_SHOVEL, Items.NETHERITE_AXE, Items.NETHERITE_HOE);
        addTagsMinMax(10.0, 1, 1, ConventionalItemTags.DYES);

        addEquipmentSet(10.0, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.WOODEN_SWORD, Items.WOODEN_PICKAXE, Items.WOODEN_SHOVEL, Items.WOODEN_AXE, Items.WOODEN_HOE);
        addEquipmentSet(6.0, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_SWORD, Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE);
        addEquipmentSet(4.0, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_SWORD, Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE);
        addEquipmentSet(2.0, Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS, Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE, Items.DIAMOND_SHOVEL, Items.DIAMOND_AXE, Items.DIAMOND_HOE);
        addEquipmentSet(1.0, Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS, Items.NETHERITE_SWORD, Items.NETHERITE_PICKAXE, Items.NETHERITE_SHOVEL, Items.NETHERITE_AXE, Items.NETHERITE_HOE);
        addEquipmentSet(6.0, Items.CROSSBOW, Items.BOW, new ItemStack(Items.ARROW, 64), Items.IRON_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.IRON_AXE, new ItemStack(Items.APPLE, 8));
        addEquipmentSet(6.0, Items.IRON_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.DIAMOND_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.STONE_SWORD, new ItemStack(Items.BREAD, 8), new ItemStack(Items.TORCH, 32));
        addEquipmentSet(8.0, Items.LEATHER_HELMET, Items.IRON_HOE, new ItemStack(Items.WHEAT_SEEDS, 32), new ItemStack(Items.PUMPKIN_SEEDS, 4), new ItemStack(Items.MELON_SEEDS, 4), new ItemStack(Items.BLUE_DYE, 8), new ItemStack(Items.DIRT, 32), Items.WATER_BUCKET, Items.WATER_BUCKET);
        addEquipmentSet(6.0, Items.IRON_HELMET, Items.DIAMOND_AXE, new ItemStack(Items.COOKED_BEEF, 16));
        addEquipmentSet(6.0, Items.TURTLE_HELMET, Items.IRON_BOOTS, Items.TRIDENT, Items.IRON_SWORD, new ItemStack(Items.BREAD, 48));
        addEquipmentSet(0.1, Items.DIAMOND_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.TRIDENT, Items.MACE, Items.IRON_AXE, new ItemStack(Items.COOKED_BEEF, 8));

        HolderSet.Named<Block> blocks = BuiltInRegistries.BLOCK.getOrThrow(BlockTags.WOOL);
        for(Holder<Block> block : blocks)
            if(RandomSource.create().nextDouble() > 0.8)
                addEquipmentSet(6.0, new ItemStack(Items.REDSTONE, 64), new ItemStack(block.value(), 16), new ItemStack(block.value(), 16), new ItemStack(block.value(), 16), new ItemStack(Blocks.REDSTONE_BLOCK, 8), new ItemStack(Blocks.REDSTONE_TORCH, 8));

        addEquipmentLevelsInOrder(Items.WOODEN_SWORD, Items.WOODEN_SWORD, Items.GOLDEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
        addEquipmentLevelsInOrder(Items.WOODEN_AXE, Items.WOODEN_AXE, Items.GOLDEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE);
        addEquipmentLevelsInOrder(Items.WOODEN_PICKAXE, Items.WOODEN_PICKAXE, Items.GOLDEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);
        addEquipmentLevelsInOrder(Items.WOODEN_SHOVEL, Items.WOODEN_SHOVEL, Items.GOLDEN_SHOVEL, Items.STONE_SHOVEL, Items.IRON_SHOVEL, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL);
        addEquipmentLevelsInOrder(Items.WOODEN_HOE, Items.WOODEN_HOE, Items.GOLDEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE);

        addPotions(buffs, 5.0, 1, 3, 20 * 30, 20 * 60, HolderSet.direct(MobEffects.HERO_OF_THE_VILLAGE, MobEffects.REGENERATION, MobEffects.TRIAL_OMEN, MobEffects.WEAVING, MobEffects.CONFUSION, MobEffects.BLINDNESS, MobEffects.HUNGER));
        addPotions(buffs, 10.0, 0, 3, 20 * 60, 20 * 60 * 10, MobEffects.REGENERATION, MobEffects.MOVEMENT_SPEED, MobEffects.DAMAGE_BOOST, MobEffects.JUMP, MobEffects.DAMAGE_RESISTANCE, MobEffects.WATER_BREATHING, MobEffects.FIRE_RESISTANCE, MobEffects.NIGHT_VISION, MobEffects.INVISIBILITY, MobEffects.ABSORPTION, MobEffects.SLOW_FALLING, MobEffects.DOLPHINS_GRACE, MobEffects.INFESTED, MobEffects.OOZING, MobEffects.WEAVING, MobEffects.WIND_CHARGED);
        addPotions(debuffs, 10.0, 0, 3, 20 * 60, 20 * 60 * 10, MobEffects.BLINDNESS, MobEffects.CONFUSION, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.DIG_SLOWDOWN, MobEffects.WEAKNESS, MobEffects.HUNGER, MobEffects.GLOWING);
        addPotions(debuffs, 10.0, 0, 2, 20 * 30, 20 * 60, MobEffects.WITHER, MobEffects.DARKNESS);

        addEnchantableArmor(10.0, Items.IRON_HELMET, Items.GOLDEN_HELMET, Items.DIAMOND_HELMET, Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.IRON_LEGGINGS, Items.GOLDEN_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.IRON_BOOTS, Items.GOLDEN_BOOTS, Items.DIAMOND_BOOTS, Items.TURTLE_HELMET);

        addEnchantableTools(10.0, Items.IRON_SWORD, Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.IRON_SHOVEL, Items.GOLDEN_SHOVEL, Items.DIAMOND_SHOVEL, Items.IRON_PICKAXE, Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE, Items.IRON_AXE, Items.GOLDEN_AXE, Items.DIAMOND_AXE, Items.BOW, Items.CROSSBOW, Items.TRIDENT);

        addEnchantableArmor(1.0, Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS);

        addEnchantableTools(1.0, Items.NETHERITE_SWORD, Items.NETHERITE_SHOVEL, Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE, Items.NETHERITE_HOE, Items.TRIDENT);

        addEnchantableTools(0.1, Items.MACE);

        addBlocks(heavyBlocks, 10.0, Blocks.ANVIL);

        addAllRandomizableBlockProperties();
    }

    public static int getRandomUnifiedSeed(RandomSource random) {
        return Math.abs(random.nextInt());
    }

    private static <T> T randomElement(Collection<T> collection, RandomSource random) {
        int num = random.nextInt(collection.size());
        int i = 0;
        for (T t : collection)
            if ((i++) == num)
                return t;
        throw new InternalError();
    }

    public static void createRandomFoodProperties(ItemStack stack, RandomSource random) {
        FoodProperties.Builder builder = new FoodProperties.Builder();
        Consumable original = stack.getOrDefault(DataComponents.CONSUMABLE, Consumables.defaultFood().build());
        Consumable.Builder consumableBuilder = Consumables.defaultFood().animation(original.animation()).sound(original.sound());
        if (random.nextBoolean()) builder.alwaysEdible();
        if (random.nextDouble() > 0.7) consumableBuilder.consumeSeconds(0.8F);
        builder.nutrition(random.nextIntBetweenInclusive(1, 10));
        builder.saturationModifier((float) (0.9 + (random.nextDouble() - random.nextDouble()) * 0.75));
        if (random.nextDouble() > 0.95) {
            List<WeightedPotion>[] posOrNegative = new List[] {buffs, debuffs};
            consumableBuilder.onConsume(new ApplyStatusEffectsConsumeEffect(WeightedSelector.selectItem(random, posOrNegative[random.nextInt(2)]).build(random), (float) random.nextGaussian()));
        }
        if (random.nextDouble() > 0.7) consumableBuilder.onConsume(new TeleportRandomlyConsumeEffect());
        stack.set(DataComponents.FOOD, builder.build());
        stack.set(DataComponents.CONSUMABLE, consumableBuilder.build());
    }

    public static BlockState getRandomBlockState(RandomSource rand, Block block, int unified) {
        BlockState state = block.defaultBlockState();

        Collection<Property<?>> randomizableProperties = randomizableBlockProperties.get(block);
        if (randomizableProperties != null) {
            if (rand.nextFloat() > 0.25)
                randomizableProperties.remove(BlockStateProperties.WATERLOGGED);
            if (unified >= 0)
                rand = RandomSource.create(unified ^ rand.nextInt(256));
            if (block.getStateDefinition().getProperties().contains(BlockStateProperties.PERSISTENT)) state.setValue(BlockStateProperties.PERSISTENT, true);

            for (Property property : randomizableProperties)
                state = state.setValue(property, PandorasBoxHelper.<Comparable>randomElement(property.getPossibleValues(), rand));
        }

        return state;
    }

    public static Collection<WeightedBlock> assembleBlocks(EitherArrayList<WeightedBlock, WeightedTag<Block>> blocks) {
        if (cachedBlockLists.containsKey(blocks)) return cachedBlockLists.get(blocks);
        Collection<WeightedBlock> output = assembleCollection(WeightedBlock::block, blockWeightedTag -> {
            List<WeightedBlock> edit = new ArrayList<>();
            Iterable<Holder<Block>> ts = BuiltInRegistries.BLOCK.getTagOrEmpty(blockWeightedTag.tagKey());
            Streams.stream(ts).forEach(blockHolder -> edit.add(new WeightedBlock(blockWeightedTag.weight(), blockHolder)));
            return edit;
        }, blocks);
        cachedBlockLists.put(blocks, output);
        return output;
    }

    public static <W extends WeightedSelector.Item, T> List<W> assembleCollection(Function<W, Holder<T>> heldGetter, Function<WeightedTag<T>, Collection<W>> mapper, EitherArrayList<W, WeightedTag<T>> selection) {
        Map<Holder<T>, W> select = new HashMap<>();
        Consumer<W> consumer = w -> {
            if (!select.containsKey(heldGetter.apply(w))) select.put(heldGetter.apply(w), w);
        };
        selection.leftSide().forEach(consumer);
        selection.rightSide().stream().map(mapper).forEach(col -> col.forEach(consumer));
        return new ArrayList<>(select.values());
    }

    public static List<RandomizedItemStack> assembleRandomisedStacks(Registry<Item> itemRegistry, EitherArrayList<RandomizedItemStack, RandomizedItemTag> selection) {
        if (cachedRandomizedStackLists.containsKey(selection)) return cachedRandomizedStackLists.get(selection);
        Map<Holder<Item>, RandomizedItemStack> select = new HashMap<>();
        Consumer<RandomizedItemStack> consumer = randomizedItemStack -> {
            if (!select.containsKey(randomizedItemStack.itemStack().getItemHolder())) select.put(randomizedItemStack.itemStack().getItemHolder(), randomizedItemStack);
        };
        selection.leftSide().forEach(consumer);
        selection.rightSide().stream().map(randomizedItemTag -> {
            List<RandomizedItemStack> edit = new ArrayList<>();
            Iterable<Holder<Item>> ts = itemRegistry.getTagOrEmpty(randomizedItemTag.items());
            Streams.stream(ts).forEach(itemHolder -> edit.add(new RandomizedItemStack(new ItemStack(itemHolder), randomizedItemTag.count().copyWithMaxCountOverride(itemHolder.value().getDefaultMaxStackSize()))));
            return edit;
        }).forEach(col -> col.forEach(consumer));
        List<RandomizedItemStack> output = new ArrayList<>(select.values());
        cachedRandomizedStackLists.put(selection, output);
        return output;
    }

    public static Block[] getRandomBlockList(RandomSource rand, Collection<WeightedBlock> selection) {
        int number = 1;
        while (number < 10 && rand.nextFloat() < 0.7f)
            number++;

        int[] weights = new int[number];
        for (int i = 0; i < number; i++) {
            weights[i] = 1;

            while (weights[i] < 10 && rand.nextFloat() < 0.7f)
                weights[i]++;
        }

        int total = 0;
        for (int i : weights)
            total += i;

        Block[] blocks = new Block[total];
        int blockIndex = 0;

        for (int i = 0; i < number; i++) {
            Block block = WeightedSelector.selectItem(rand, selection).block().value();

            for (int j = 0; j < weights[i]; j++) {
                blocks[blockIndex] = block;
                blockIndex++;
            }
        }

        return blocks;
    }

    public static Block getRandomBlock(RandomSource rand, Collection<WeightedBlock> randomBlockList) {
        if (randomBlockList != null && !randomBlockList.isEmpty())
            return WeightedSelector.selectItem(rand, randomBlockList).block().value();

        return WeightedSelector.selectItem(rand, assembleBlocks(blocks)).block().value();
    }

    public static WeightedEntity[] getRandomEntityList(RandomSource rand, Collection<WeightedEntity> selection) {
        WeightedEntity[] entities = new WeightedEntity[rand.nextInt(5) + 1];

        for (int i = 0; i < entities.length; i++)
            entities[i] = getRandomEntityFromList(rand, selection);

        return entities;
    }

    public static WeightedEntity getRandomEntityFromList(RandomSource rand, Collection<WeightedEntity> entityList) {
        return WeightedSelector.selectItem(rand, entityList);
    }

    public static ItemStack getRandomWeaponItemForLevel(RandomSource random, int level) {
        Set<Item> itemSet = equipmentForLevels.keySet();
        Item[] itemArray = itemSet.toArray(new Item[0]);

        return getWeaponItemForLevel(itemArray[random.nextInt(itemArray.length)], level);
    }

    public static ItemStack getWeaponItemForLevel(Item baseItem, int level) {
        Hashtable<Integer, ItemStack> levels = equipmentForLevels.get(baseItem);

        if (levels != null) {
            while (level > 0) {
                if (levels.containsKey(level))
                    return levels.get(level);

                level--;
            }
        }

        return null;
    }
}
