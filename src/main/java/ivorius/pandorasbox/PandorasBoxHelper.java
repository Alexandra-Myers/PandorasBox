/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import ivorius.pandorasbox.init.Init;
import ivorius.pandorasbox.init.ItemInit;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import ivorius.pandorasbox.weighted.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class PandorasBoxHelper
{
    public static List<WeightedEntity> landMobs = new ArrayList<>();
    public static List<WeightedEntity> mobs = new ArrayList<>();
    public static List<WeightedEntity> creatures = new ArrayList<>();
    public static List<WeightedEntity> waterCreatures = new ArrayList<>();
    public static List<WeightedEntity> waterMobs = new ArrayList<>();
    public static List<WeightedEntity> tameableCreatures = new ArrayList<>();

    public static List<RandomizedItemStack> blocksAndItems = new ArrayList<>();
    public static Multimap<Block, Property<?>> randomizableBlockProperties = HashMultimap.create();

    public static List<WeightedBlock> blocks = new ArrayList<>();

    public static List<RandomizedItemStack> items = new ArrayList<>();
    public static List<WeightedSet> equipmentSets = new ArrayList<>();
    public static Hashtable<Item, Hashtable<Integer, ItemStack>> equipmentForLevels = new Hashtable<>();

    public static List<WeightedPotion> buffs = new ArrayList<>();
    public static List<WeightedPotion> debuffs = new ArrayList<>();

    public static List<RandomizedItemStack> enchantableArmorList = new ArrayList<>();
    public static List<RandomizedItemStack> enchantableToolList = new ArrayList<>();

    public static List<WeightedBlock> heavyBlocks = new ArrayList<>();

    public static void addEntities(List<WeightedEntity> list, double weight, int minNumber, int maxNumber, String... entities) {
        for (String s : entities) {
            list.add(new WeightedEntity(weight, s, minNumber, maxNumber));
        }
    }

    public static void addBlocks(double weight, Block... blocks) {
        for (Block block : blocks) {
            PandorasBoxHelper.blocks.add(new WeightedBlock(weight, block));

            Item item = block.asItem();
            if (item != null)
                addItem(new RandomizedItemStack(item, 1, item.getMaxStackSize(), weight));
        }
    }
    public static void addBlocks(double weight, List<Block> blocks) {
        for (Block block : blocks) {
            PandorasBoxHelper.blocks.add(new WeightedBlock(weight, block));

            Item item = block.asItem();
            if (item != null)
                addItem(new RandomizedItemStack(item, 1, item.getMaxStackSize(), weight));
        }
    }

    public static void addBlocks(List<WeightedBlock> list, double weight, Block... blocks) {
        for (Block block : blocks) {
            list.add(new WeightedBlock(weight, block));
        }
    }

    public static void addItem(RandomizedItemStack RandomizedItemStack) {
        items.add(RandomizedItemStack);
        blocksAndItems.add(RandomizedItemStack);
    }

    public static void addItems(double weight, Object... items) {
        for (Object object : items) {
            if (object instanceof Item item) {
                addItem(new RandomizedItemStack(item, 1, item.getMaxStackSize(), weight));
            } else if (object instanceof ItemStack itemStack) {
                addItem(new RandomizedItemStack(itemStack, 1, itemStack.getMaxStackSize(), weight));
            }
        }
    }

    public static void addItemsMinMax(double weight, int min, int max, Object... items) {
        for (Object object : items) {
            if (object instanceof Item item) {
                addItem(new RandomizedItemStack(item, min, max, weight));
            } else if (object instanceof ItemStack itemStack) {
                addItem(new RandomizedItemStack(itemStack, min, max, weight));
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

    public static void addPotions(List<WeightedPotion> list, double weight, int minStrength, int maxStrength, int minDuration, int maxDuration, MobEffect... potions) {
        for (MobEffect effect : potions) {
            list.add(new WeightedPotion(weight, effect, minStrength, maxStrength, minDuration, maxDuration));
        }
    }

    public static void addEnchantableArmor(double weight, Object... items) {
        for (Object object : items) {
            if (object instanceof Item item) {
                enchantableArmorList.add(new RandomizedItemStack(item, 1, 1, weight));
            } else if (object instanceof ItemStack itemStack) {
                enchantableArmorList.add(new RandomizedItemStack(itemStack, 1, 1, weight));
            }
        }
    }

    public static void addEnchantableTools(double weight, Object... items) {
        for (Object object : items) {
            if (object instanceof Item item) {
                enchantableToolList.add(new RandomizedItemStack(item, 1, 1, weight));
            } else if (object instanceof ItemStack itemStack) {
                enchantableToolList.add(new RandomizedItemStack(itemStack, 1, 1, weight));
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

    public static void addAllRandomizableBlockProperties(List<Block> blocks) {
        for (Block block : blocks)
            randomizableBlockProperties.putAll(block, block.defaultBlockState().getProperties());
    }

    public static void initialize() {
        mobs = new ArrayList<>();
        creatures = new ArrayList<>();
        waterCreatures = new ArrayList<>();
        waterMobs = new ArrayList<>();
        tameableCreatures = new ArrayList<>();
        blocks = new ArrayList<>();
        randomizableBlockProperties = HashMultimap.create();
        blocksAndItems = new ArrayList<>();
        items = new ArrayList<>();
        equipmentSets = new ArrayList<>();
        equipmentForLevels = new Hashtable<>();
        buffs = new ArrayList<>();
        debuffs = new ArrayList<>();
        enchantableArmorList = new ArrayList<>();
        enchantableToolList = new ArrayList<>();
        heavyBlocks = new ArrayList<>();
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

        ArrayListExtensions<Block> planks = new ArrayListExtensions<>();
        ArrayListExtensions<Block> glass = new ArrayListExtensions<>();
        ArrayListExtensions<Block> randomizable = new ArrayListExtensions<>();
        ArrayListExtensions<Item> misc = new ArrayListExtensions<>();
        ArrayListExtensions<Item> records = new ArrayListExtensions<>();
        ArrayListExtensions<Item> dyes = new ArrayListExtensions<>();
        glass.addAll(Blocks.COAL_ORE, Blocks.LAPIS_ORE, Blocks.REDSTONE_ORE, Blocks.NETHER_QUARTZ_ORE, Blocks.GLASS);
        planks.addAll(Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.DEEPSLATE, Blocks.TUFF, Blocks.SANDSTONE, Blocks.SAND, Blocks.RED_SAND, Blocks.RED_SANDSTONE);
        randomizable.addAll(
                Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.DEEPSLATE, Blocks.TUFF, Blocks.DIRT, Blocks.SAND, Blocks.RED_SAND, Blocks.STONE_BRICK_STAIRS,
                Blocks.QUARTZ_BLOCK, Blocks.QUARTZ_STAIRS,
                Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_QUARTZ_STAIRS,
                Blocks.SANDSTONE, Blocks.SANDSTONE_STAIRS,
                Blocks.RED_SANDSTONE, Blocks.RED_SANDSTONE_STAIRS,
                Blocks.RAIL, Blocks.POWERED_RAIL, Blocks.ACTIVATOR_RAIL, Blocks.DETECTOR_RAIL,
                Blocks.FURNACE, Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN,
                Blocks.SNOW, Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW,
                Blocks.CHEST, Blocks.ENDER_CHEST, Blocks.TRAPPED_CHEST,
                Blocks.SPONGE);
        planks.addAll(PandorasBox.planks, PandorasBox.logs, PandorasBox.leaves, PandorasBox.wool, PandorasBox.slabs, PandorasBox.bricks, PandorasBox.stained_terracotta);
        randomizable.addAll(PandorasBox.planks, PandorasBox.logs, PandorasBox.leaves, PandorasBox.wool, PandorasBox.slabs, PandorasBox.bricks, PandorasBox.stained_terracotta, PandorasBox.stained_glass, PandorasBox.saplings, PandorasBox.pots);
        glass.addAll(Blocks.SOUL_SAND, Blocks.SOUL_SOIL);
        glass.addAll(PandorasBox.stained_glass);
        misc.addAll(Items.LAVA_BUCKET, Items.MILK_BUCKET, Items.WATER_BUCKET, Items.FLINT_AND_STEEL, Items.PAINTING, Items.FLOWER_POT, Items.MINECART, Items.CAULDRON, Items.BRUSH);
        for(Item item : BuiltInRegistries.ITEM.stream().toList()) {
            ItemStack stack = new ItemStack(item);
            if(stack.is(ItemTags.BOATS) || stack.is(ItemTags.BEDS))
                misc.add(item);
            if(stack.is(ItemTags.MUSIC_DISCS))
                records.add(item);
            if(item instanceof DyeItem)
                dyes.add(item);
        }

        addBlocks(40.0, planks);
        addBlocks(15.0, Blocks.PRISMARINE, Blocks.QUARTZ_BLOCK);
        addBlocks(10.0, Blocks.COBBLESTONE, Blocks.MOSSY_COBBLESTONE, Blocks.COBBLED_DEEPSLATE, Blocks.NETHERRACK, Blocks.NETHER_BRICKS, Blocks.BRICKS, Blocks.END_STONE, Blocks.END_STONE_BRICKS, Blocks.TERRACOTTA);
        addBlocks(10.0, Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.PUMPKIN, Blocks.CLAY, Blocks.MYCELIUM);
        addBlocks(8.0, glass);
        addBlocks(0.2, Blocks.NETHERITE_BLOCK, Blocks.DIAMOND_BLOCK, Blocks.EMERALD_BLOCK, Blocks.GOLD_BLOCK, Blocks.LODESTONE);
        addBlocks(0.3, Blocks.IRON_BLOCK);
        addBlocks(0.5, Blocks.ANCIENT_DEBRIS, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.GOLD_ORE);
        addBlocks(1.0, Blocks.IRON_ORE);
        addBlocks(2.0, Blocks.TNT, Blocks.GLOWSTONE, Blocks.SHROOMLIGHT, Blocks.COAL_BLOCK, Blocks.LAPIS_BLOCK, Blocks.REDSTONE_BLOCK, Blocks.SLIME_BLOCK, Blocks.SPONGE);
        addBlocks(5.0, Blocks.DRAGON_EGG, Blocks.REDSTONE_LAMP, Blocks.SEA_LANTERN, Blocks.SNOW, Blocks.BOOKSHELF, Blocks.JACK_O_LANTERN, Blocks.HAY_BLOCK, Blocks.OBSIDIAN, Blocks.MELON, Blocks.CHISELED_BOOKSHELF);

        addItems(10.0, Items.COAL, Items.GUNPOWDER, Items.WHEAT, Items.SADDLE, Items.REDSTONE, Items.BONE, Items.MELON, Items.CLAY_BALL, Items.BOOK, Items.GOLD_NUGGET, Items.POTATO, Items.BUCKET, Items.STICK, Items.STRING, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.WHEAT_SEEDS, Items.SNOWBALL, Items.SUGAR, Items.FISHING_ROD, Items.NETHER_WART, Items.FLINT, Items.EGG, Items.BRICK, Items.PAPER, Items.TORCH);
        addItems(10.0, ItemInit.PBI);
        addItems(10.0, Items.CHICKEN, Items.COOKED_CHICKEN, Items.BEEF, Items.PUMPKIN_PIE, Items.COOKED_BEEF, Items.MUSHROOM_STEW, Items.ROTTEN_FLESH, Items.CARROT, Items.PORKCHOP, Items.COOKED_PORKCHOP, Items.APPLE, Items.CAKE, Items.BREAD, Items.COOKIE, Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.PUFFERFISH, Items.MUTTON, Items.COOKED_MUTTON, Items.RABBIT, Items.RABBIT_FOOT, Items.RABBIT_HIDE, Items.RABBIT_STEW, Items.COOKED_RABBIT, Items.HONEY_BOTTLE, Items.SWEET_BERRIES);
        addItems(8.0,  misc);
        addItems(8.0, Items.NAME_TAG);
        addItems(6.0, Items.IRON_INGOT, Items.GLOWSTONE_DUST, Items.BLAZE_POWDER, Items.BLAZE_ROD, Items.CLOCK, Items.GHAST_TEAR, Items.ENDER_EYE, Items.GLISTERING_MELON_SLICE, Items.SPIDER_EYE, Items.FERMENTED_SPIDER_EYE, Items.MAGMA_CREAM, Items.GOLDEN_CARROT);
        addItems(4.0, Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS, Items.WOODEN_SWORD, Items.WOODEN_PICKAXE, Items.WOODEN_SHOVEL, Items.WOODEN_AXE, Items.WOODEN_HOE);
        addItems(4.0, Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS, Items.GOLDEN_SWORD, Items.GOLDEN_PICKAXE, Items.GOLDEN_SHOVEL, Items.GOLDEN_AXE, Items.GOLDEN_HOE);
        addItems(4.0, Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS, Items.IRON_SWORD, Items.IRON_PICKAXE, Items.IRON_SHOVEL, Items.IRON_AXE, Items.IRON_HOE);
        addItems(3.0, Items.IRON_HORSE_ARMOR, Items.GOLDEN_HORSE_ARMOR);
        addItems(2.0, Items.DIAMOND_HORSE_ARMOR);
        addItemsMinMax(2.0, 1, 1, Items.NETHER_STAR, Items.BEACON, Items.ANVIL, Items.BREWING_STAND, Items.DISPENSER, Items.ENDER_CHEST, Items.JUKEBOX, Items.ENCHANTING_TABLE);
        addItemsMinMax(5.0, 1, 1, Items.CHEST, Items.BARREL);
        addItems(2.0, Items.DIAMOND, Items.EMERALD, Items.GOLD_INGOT, Items.GOLDEN_APPLE, Items.ENDER_PEARL, Items.PRISMARINE_CRYSTALS, Items.PRISMARINE_SHARD);
        addItems(0.5, Items.NETHERITE_SCRAP, Items.DISC_FRAGMENT_5, Items.ECHO_SHARD, Items.RECOVERY_COMPASS);
        addItems(2.0, Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS, Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE, Items.DIAMOND_SHOVEL, Items.DIAMOND_AXE, Items.DIAMOND_HOE);
        addItems(0.2, Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS, Items.NETHERITE_SWORD, Items.NETHERITE_PICKAXE, Items.NETHERITE_SHOVEL, Items.NETHERITE_AXE, Items.NETHERITE_HOE);
        addItems(2.0, records);
        for (Item dye : dyes)
            addItems(10.0, new ItemStack(dye, 1));

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
        for(Block block : PandorasBox.wool)
            if(RandomSource.create().nextDouble() > 0.8)
                addEquipmentSet(6.0, new ItemStack(Items.REDSTONE, 64), new ItemStack(block, 16), new ItemStack(block, 16), new ItemStack(block, 16), new ItemStack(Blocks.REDSTONE_BLOCK, 8), new ItemStack(Blocks.REDSTONE_TORCH, 8));

        addEquipmentLevelsInOrder(Items.WOODEN_SWORD, Items.WOODEN_SWORD, Items.GOLDEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
        addEquipmentLevelsInOrder(Items.WOODEN_AXE, Items.WOODEN_AXE, Items.GOLDEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE);
        addEquipmentLevelsInOrder(Items.WOODEN_PICKAXE, Items.WOODEN_PICKAXE, Items.GOLDEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);
        addEquipmentLevelsInOrder(Items.WOODEN_SHOVEL, Items.WOODEN_SHOVEL, Items.GOLDEN_SHOVEL, Items.STONE_SHOVEL, Items.IRON_SHOVEL, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL);
        addEquipmentLevelsInOrder(Items.WOODEN_HOE, Items.WOODEN_HOE, Items.GOLDEN_HOE, Items.STONE_HOE, Items.IRON_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE);

        addPotions(buffs, 10.0, 0, 3, 20 * 60, 20 * 60 * 10, MobEffects.REGENERATION, MobEffects.MOVEMENT_SPEED, MobEffects.DAMAGE_BOOST, MobEffects.JUMP, MobEffects.DAMAGE_RESISTANCE, MobEffects.WATER_BREATHING, MobEffects.FIRE_RESISTANCE, MobEffects.NIGHT_VISION, MobEffects.INVISIBILITY, MobEffects.ABSORPTION, MobEffects.SLOW_FALLING, MobEffects.DOLPHINS_GRACE);
        addPotions(debuffs, 10.0, 0, 3, 20 * 60, 20 * 60 * 10, MobEffects.BLINDNESS, MobEffects.CONFUSION, MobEffects.MOVEMENT_SLOWDOWN, MobEffects.DIG_SLOWDOWN, MobEffects.WEAKNESS, MobEffects.HUNGER, MobEffects.GLOWING);
        addPotions(debuffs, 10.0, 0, 2, 20 * 30, 20 * 60, MobEffects.WITHER, MobEffects.DARKNESS);

        addEnchantableArmor(10.0, Items.IRON_HELMET, Items.GOLDEN_HELMET, Items.DIAMOND_HELMET, Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.IRON_LEGGINGS, Items.GOLDEN_LEGGINGS, Items.DIAMOND_LEGGINGS, Items.IRON_BOOTS, Items.GOLDEN_BOOTS, Items.DIAMOND_BOOTS, Items.TURTLE_HELMET);

        addEnchantableTools(10.0, Items.IRON_SWORD, Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.IRON_SHOVEL, Items.GOLDEN_SHOVEL, Items.DIAMOND_SHOVEL, Items.IRON_PICKAXE, Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE, Items.IRON_AXE, Items.GOLDEN_AXE, Items.DIAMOND_AXE, Items.BOW, Items.CROSSBOW, Items.TRIDENT);

        addEnchantableArmor(1.0, Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS);

        addEnchantableTools(1.0, Items.NETHERITE_SWORD, Items.NETHERITE_SHOVEL, Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE, Items.NETHERITE_HOE);

        addBlocks(heavyBlocks, 10.0, Blocks.ANVIL);

        addAllRandomizableBlockProperties(
                randomizable
        );
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

    public static BlockState getRandomBlockState(RandomSource rand, Block block, int unified) {
        BlockState state = block.defaultBlockState();

        Collection<Property<?>> randomizableProperties = randomizableBlockProperties.get(block);
        if (randomizableProperties != null) {
            if (rand.nextFloat() > 0.25)
                randomizableProperties.remove(BlockStateProperties.WATERLOGGED);
            if (unified >= 0)
                rand = RandomSource.create(unified ^ rand.nextInt(256));

            for (Property property : randomizableProperties)
                state = state.setValue(property, PandorasBoxHelper.<Comparable>randomElement(property.getPossibleValues(), rand));
        }

        return state;
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
            Block block = WeightedSelector.selectItem(rand, selection).block;

            for (int j = 0; j < weights[i]; j++) {
                blocks[blockIndex] = block;
                blockIndex++;
            }
        }

        return blocks;
    }

    public static Block getRandomBlock(RandomSource rand, Collection<WeightedBlock> randomBlockList) {
        if (randomBlockList != null && randomBlockList.size() > 0)
            return WeightedSelector.selectItem(rand, randomBlockList).block;

        return WeightedSelector.selectItem(rand, blocks).block;
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
        Item[] itemArray = itemSet.toArray(new Item[itemSet.size()]);

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
