/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox;

import com.google.common.collect.*;
import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import ivorius.pandorasbox.init.MobEffectInit;
import ivorius.pandorasbox.init.PandoraBlockTags;
import ivorius.pandorasbox.init.PandoraItemTags;
import ivorius.pandorasbox.random.ILinear;
import ivorius.pandorasbox.utils.*;
import ivorius.pandorasbox.weighted.*;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
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

    public static EitherArrayList<RandomizedItemStack, RandomizedItemTag> blocksAndItems = new EitherArrayList<>();
    public static Multimap<Block, Property<?>> randomizableBlockProperties = HashMultimap.create();

    public static EitherArrayList<WeightedBlock, WeightedTag<Block>> blocks = new EitherArrayList<>();

    public static Hashtable<Item, Hashtable<Integer, ItemStack>> equipmentForLevels = new Hashtable<>();

    public static List<WeightedPotion> buffs = new ArrayList<>();
    public static List<WeightedPotion> debuffs = new ArrayList<>();

    @SafeVarargs
    public static void addBlockTags(double weight, TagKey<Block>... blocks) {
        for (TagKey<Block> blockTagKey : blocks) {
            PandorasBoxHelper.blocks.add(Either.right(new WeightedTag<>(weight, blockTagKey)));

            blocksAndItems.add(Either.right(new RandomizedItemTag(Either.right(blockTagKey), new WeightedWithRandomCount(1, Optional.empty(), weight))));
        }
    }

    public static void addBlocks(double weight, Block... blocks) {
        for (Block block : blocks) {
            PandorasBoxHelper.blocks.add(Either.left(new WeightedBlock(weight, block)));

            Item item = block.asItem();
            blocksAndItems.add(Either.left(new RandomizedItemStack(item, 1, item.getDefaultMaxStackSize(), weight)));
        }
    }

    public static void addTag(RandomizedItemTag randomizedItemTag) {
        blocksAndItems.add(Either.right(randomizedItemTag));
    }

    @SafeVarargs
    public static void addTags(double weight, TagKey<Item>... tags) {
        for (TagKey<Item> tagKey : tags) {
            addTag(new RandomizedItemTag(Either.left(tagKey), new WeightedWithRandomCount(1, Optional.empty(), weight)));
        }
    }

    @SafeVarargs
    public static void addTagsMinMax(double weight, int min, int max, TagKey<Item>... tags) {
        for (TagKey<Item> tagKey : tags) {
            addTag(new RandomizedItemTag(Either.left(tagKey), new WeightedWithRandomCount(min, max, weight)));
        }
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
        blocks.clear();
        randomizableBlockProperties.clear();
        blocksAndItems.clear();
        equipmentForLevels.clear();
        buffs.clear();
        debuffs.clear();

        addBlockTags(30.0, BlockTags.PLANKS, BlockTags.WOOL, BlockTags.LEAVES, BlockTags.LOGS, BlockTags.STONE_BRICKS);
        addBlocks(15.0, Blocks.PRISMARINE, Blocks.QUARTZ_BLOCK, Blocks.SMOOTH_QUARTZ);
        addBlocks(10.0, Blocks.GRAVEL, Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.CLAY, Blocks.POLISHED_DEEPSLATE, Blocks.DEEPSLATE_TILES, Blocks.NETHER_BRICKS, Blocks.BRICKS, Blocks.END_STONE, Blocks.END_STONE_BRICKS);
        addBlockTags(10.0, ConventionalBlockTags.COBBLESTONES, BlockTags.NYLIUM, PandoraBlockTags.ALL_TERRACOTTA, ConventionalBlockTags.PLAYER_WORKSTATIONS_CRAFTING_TABLES, ConventionalBlockTags.PLAYER_WORKSTATIONS_FURNACES, BlockTags.BASE_STONE_NETHER, BlockTags.DIRT, BlockTags.SLABS, BlockTags.STAIRS);
        addBlockTags(8.0, BlockTags.SAND, ConventionalBlockTags.STONES, BlockTags.WITHER_SUMMON_BASE_BLOCKS, ConventionalBlockTags.QUARTZ_ORES, BlockTags.COAL_ORES, BlockTags.COPPER_ORES, BlockTags.LAPIS_ORES, BlockTags.REDSTONE_ORES, BlockTags.IRON_ORES, BlockTags.SNOW, ConventionalBlockTags.CHESTS, ConventionalBlockTags.BARRELS, ConventionalBlockTags.SANDSTONE_BLOCKS, ConventionalBlockTags.VILLAGER_JOB_SITES, BlockTags.RAILS, ConventionalBlockTags.CONCRETES, BlockTags.CONCRETE_POWDER, BlockTags.SAPLINGS, BlockTags.FLOWER_POTS, ConventionalBlockTags.GLASS_BLOCKS, ConventionalBlockTags.GLASS_PANES);
        addBlocks(5.0, Blocks.DRAGON_EGG, Blocks.NOTE_BLOCK, Blocks.REDSTONE_LAMP, Blocks.SEA_LANTERN, Blocks.SNOW, Blocks.BOOKSHELF, Blocks.JACK_O_LANTERN, Blocks.MELON, Blocks.CHISELED_BOOKSHELF);
        addBlockTags(5.0, ConventionalBlockTags.STORAGE_BLOCKS_WHEAT, ConventionalBlockTags.STORAGE_BLOCKS_DRIED_KELP, ConventionalBlockTags.NORMAL_OBSIDIANS, ConventionalBlockTags.CRYING_OBSIDIANS);
        addBlocks(2.0, Blocks.LODESTONE, Blocks.TNT, Blocks.GLOWSTONE, Blocks.SHROOMLIGHT, Blocks.SPONGE);
        addBlockTags(2.0, ConventionalBlockTags.STORAGE_BLOCKS_COAL, ConventionalBlockTags.STORAGE_BLOCKS_COPPER, ConventionalBlockTags.STORAGE_BLOCKS_LAPIS, ConventionalBlockTags.STORAGE_BLOCKS_REDSTONE, ConventionalBlockTags.STORAGE_BLOCKS_SLIME, ConventionalBlockTags.STORAGE_BLOCKS_RESIN);
        addBlockTags(0.5, ConventionalBlockTags.NETHERITE_SCRAP_ORES, BlockTags.DIAMOND_ORES, BlockTags.EMERALD_ORES, BlockTags.GOLD_ORES);
        addBlockTags(0.2, ConventionalBlockTags.STORAGE_BLOCKS_NETHERITE, ConventionalBlockTags.STORAGE_BLOCKS_DIAMOND, ConventionalBlockTags.STORAGE_BLOCKS_EMERALD, ConventionalBlockTags.STORAGE_BLOCKS_GOLD, ConventionalBlockTags.STORAGE_BLOCKS_IRON);

        addTags(10.0, PandoraItemTags.PANDORA_ITEMS_MISC_COMMON);
        addTagsMinMax(10.0, 1, 1, ConventionalItemTags.DYES);
        addTags(8.0, PandoraItemTags.PANDORA_ITEMS_MISC_UNCOMMON);
        addTags(6.0, PandoraItemTags.PANDORA_ITEMS_MISC_RARE);
        addTags(4.0, PandoraItemTags.PANDORA_ITEMS_EQUIPMENT_COMMON);
        addTags(4.0, PandoraItemTags.PANDORA_ITEMS_MISC_VERY_RARE);
        addTags(3.0, PandoraItemTags.PANDORA_ITEMS_EQUIPMENT_UNCOMMON);
        addTagsMinMax(5.0, 1, 1, PandoraItemTags.PANDORA_ITEMS_SINGLES_VERY_RARE);
        addTagsMinMax(2.0, 1, 1, PandoraItemTags.PANDORA_ITEMS_SINGLES_EPIC);
        addTags(2.0, PandoraItemTags.PANDORA_ITEMS_MISC_EPIC);
        addTagsMinMax(0.5, 1, 5, PandoraItemTags.PANDORA_ITEMS_LEGENDARY);
        addTags(1.0, PandoraItemTags.PANDORA_ITEMS_DRAGON);
        addTags(2.0, PandoraItemTags.PANDORA_ITEMS_EQUIPMENT_RARE);
        addTags(0.2, PandoraItemTags.PANDORA_ITEMS_EQUIPMENT_VERY_RARE);

        addEquipmentLevelsInOrder(Items.WOODEN_SWORD, Items.WOODEN_SWORD, Items.GOLDEN_SWORD, Items.STONE_SWORD, Items.COPPER_SWORD, Items.IRON_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD);
        addEquipmentLevelsInOrder(Items.WOODEN_AXE, Items.WOODEN_AXE, Items.GOLDEN_AXE, Items.STONE_AXE, Items.COPPER_AXE, Items.IRON_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE);
        addEquipmentLevelsInOrder(Items.WOODEN_PICKAXE, Items.WOODEN_PICKAXE, Items.GOLDEN_PICKAXE, Items.STONE_PICKAXE, Items.COPPER_PICKAXE, Items.IRON_PICKAXE, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE);
        addEquipmentLevelsInOrder(Items.WOODEN_SHOVEL, Items.WOODEN_SHOVEL, Items.GOLDEN_SHOVEL, Items.STONE_SHOVEL, Items.COPPER_SHOVEL, Items.IRON_SHOVEL, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL);
        addEquipmentLevelsInOrder(Items.WOODEN_HOE, Items.WOODEN_HOE, Items.GOLDEN_HOE, Items.STONE_HOE, Items.COPPER_HOE, Items.IRON_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE);

        addPotions(buffs, 5.0, 1, 3, 20 * 30, 20 * 60, HolderSet.direct(MobEffects.HERO_OF_THE_VILLAGE, MobEffects.REGENERATION, MobEffects.TRIAL_OMEN, MobEffects.WEAVING, MobEffects.NAUSEA, MobEffects.BLINDNESS, MobEffects.HUNGER));
        addPotions(buffs, 10.0, 0, 3, 20 * 60, 20 * 60 * 10, MobEffects.REGENERATION, MobEffects.SPEED, MobEffects.STRENGTH, MobEffects.JUMP_BOOST, MobEffects.RESISTANCE, MobEffects.WATER_BREATHING, MobEffects.FIRE_RESISTANCE, MobEffects.NIGHT_VISION, MobEffects.INVISIBILITY, MobEffects.ABSORPTION, MobEffects.SLOW_FALLING, MobEffects.DOLPHINS_GRACE, MobEffects.INFESTED, MobEffects.OOZING, MobEffects.WEAVING, MobEffects.WIND_CHARGED);
        addPotions(debuffs, 10.0, 0, 3, 20 * 60, 20 * 60 * 10, MobEffects.BLINDNESS, MobEffects.NAUSEA, MobEffects.SLOWNESS, MobEffects.MINING_FATIGUE, MobEffects.WEAKNESS, MobEffects.HUNGER, MobEffects.GLOWING);
        addPotions(debuffs, 10.0, 0, 2, 20 * 30, 20 * 60, MobEffects.WITHER, MobEffects.DARKNESS);
        addPotions(debuffs, 6.0, 0, 3, 20 * 30, 20 * 45, MobEffectInit.SHRUNK);

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

    public static List<RandomizedItemStack> assembleRandomisedStacks(Registry<Item> itemRegistry, Registry<Block> blockRegistry, EitherArrayList<RandomizedItemStack, RandomizedItemTag> selection) {
        if (cachedRandomizedStackLists.containsKey(selection)) return cachedRandomizedStackLists.get(selection);
        Map<Holder<Item>, RandomizedItemStack> select = new HashMap<>();
        Consumer<RandomizedItemStack> consumer = randomizedItemStack -> {
            if (!select.containsKey(randomizedItemStack.itemStack().getItemHolder())) select.put(randomizedItemStack.itemStack().getItemHolder(), randomizedItemStack);
        };
        selection.leftSide().forEach(consumer);
        selection.rightSide().stream().map(randomizedItemTag -> {
            List<RandomizedItemStack> edit = new ArrayList<>();
            List<Item> ts = randomizedItemTag.items().map(itemTagKey -> Streams.stream(itemRegistry.getTagOrEmpty(itemTagKey)).map(Holder::value).toList(), blockTagKey -> Streams.stream(blockRegistry.getTagOrEmpty(blockTagKey)).map(blockHolder -> blockHolder.value().asItem()).toList());
            ts.forEach(item -> {
                ItemStack stack = new ItemStack(item);
                stack.applyComponents(randomizedItemTag.patch());
                edit.add(new RandomizedItemStack(stack, randomizedItemTag.count().copyWithMaxCountOverride(stack.getMaxStackSize())));
            });
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
