package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effectcreators.*;
import ivorius.pandorasbox.random.*;
import ivorius.pandorasbox.utils.RandomizedItemStack;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedEntity;
import ivorius.pandorasbox.weighted.WeightedPotion;
import net.atlas.atlascore.util.ArrayListExtensions;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by lukas on 01.12.14.
 */
public class PBEffects {
    public static void registerEffectCreators() {
        PandorasBoxHelper.initialize();

        PBECRegistry.register(new PBECDuplicateBox(new IConstant(PBEffectDuplicateBox.MODE_BOX_IN_BOX), new DConstant(0.5)), "matryoshka");

        PBECRegistry.register(new PBECSpawnEntities(new ILinear(20, 100), new ILinear(4, 20), new IConstant(1), new IConstant(0), new IConstant(0), new IConstant(0), PandorasBoxHelper.mobs), "mobs");
        PBECRegistry.register(new PBECSpawnEntities(new ILinear(20, 100), new ILinear(2, 6), new ILinear(2, 5), new IConstant(0), new IConstant(0), new IConstant(0), PandorasBoxHelper.mobs), "mob_towers");
        PBECRegistry.register(new PBECSpawnBlocks(new ILinear(10, 30), new ILinear(0, 10), PandorasBoxHelper.heavyBlocks, null, PBECSpawnBlocks.defaultShowerSpawn()), "megaton");
        PBECRegistry.register(new PBECSpawnBlocks(new ILinear(30, 100), new ILinear(0, 10), PandorasBoxHelper.blocks, null, PBECSpawnBlocks.defaultShowerSpawn()), "block_grave");
        PBECRegistry.register(new PBECSpawnBlocks(new ILinear(30, 100), new ILinear(0, 10), PandorasBoxHelper.blocks), "block_shower");
        PBECRegistry.register(new PBECTransform(new DLinear(10.0, 80.0), PandorasBoxHelper.blocks), "transform");
        PBECRegistry.register(new PBECReplace(new DLinear(10.0, 80.0), null, PandorasBoxHelper.blocks, new ZConstant(true)), "replace");
        PBECRegistry.register(new PBECReplace(new DLinear(40.0, 120.0), new Block[]{Blocks.WATER, Blocks.LAVA}, List.of(new WeightedBlock(100, Blocks.AIR)), new ZConstant(false)), "dryness");
        PBECRegistry.register(new PBECSpawnTNT(new ILinear(20, 100), new ILinear(5, 50), new ILinear(20, 1000), new ValueThrow(new DLinear(0.2, 2.0), new DLinear(0.5, 5.0)), new ValueSpawn(new DLinear(5.0, 50.0), new DConstant(0.0))), "tntsplosion");
        PBECRegistry.register(new PBECMulti(new PBECSpawnManySameItems(new ILinear(10, 30), PandorasBoxHelper.blocksAndItems), 0, new PBECSpawnTNT(new ILinear(20, 60), new ILinear(1, 10), new ILinear(20, 60), new ValueThrow(new DLinear(0.2, 0.5), new DLinear(0.3, 0.5)), new ValueSpawn(new DLinear(3.0, 10.0), new DConstant(0.0))), 60), "dirty_trick");
        PBECRegistry.register(new PBECPool(new DLinear(10.0, 30.0), Blocks.WATER, PandorasBoxHelper.blocks), "water_pool");
        PBECRegistry.register(new PBECPool(new DLinear(10.0, 30.0), Blocks.LAVA, PandorasBoxHelper.blocks), "lava_pool");
        WeightedBlock[][] blockArray = new WeightedBlock[][]{
                new WeightedBlock[] { new WeightedBlock(100, Blocks.STONE_BRICKS),
                    new WeightedBlock(90, Blocks.CRACKED_STONE_BRICKS),
                    new WeightedBlock(10, Blocks.CHISELED_STONE_BRICKS),
                    new WeightedBlock(90, Blocks.STONE_BRICK_SLAB),
                    new WeightedBlock(90, Blocks.STONE_BRICK_STAIRS) },
                new WeightedBlock[] { new WeightedBlock(100, Blocks.POLISHED_BLACKSTONE_BRICKS),
                    new WeightedBlock(90, Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS),
                    new WeightedBlock(10, Blocks.CHISELED_POLISHED_BLACKSTONE),
                    new WeightedBlock(90, Blocks.POLISHED_BLACKSTONE_BRICK_SLAB),
                    new WeightedBlock(90, Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS) }
        };
        ArrayListExtensions<RandomizedItemStack> loot = new ArrayListExtensions<>(Arrays.asList(new RandomizedItemStack(Items.GOLD_NUGGET, 1, 5, 60), new RandomizedItemStack(Items.IRON_NUGGET, 1, 3, 35), new RandomizedItemStack(Items.FLINT, 1, 3, 80), new RandomizedItemStack(Items.FLINT_AND_STEEL, 1, 1, 20), new RandomizedItemStack(Items.OBSIDIAN, 1, 3, 10), new RandomizedItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1, 3, 5), new RandomizedItemStack(Items.GOLDEN_AXE, 1, 1, 20), new RandomizedItemStack(Items.GOLDEN_SWORD, 1, 1, 20), new RandomizedItemStack(Items.GOLDEN_HOE, 1, 1, 20), new RandomizedItemStack(Items.GOLDEN_PICKAXE, 1, 1, 20), new RandomizedItemStack(Items.GOLDEN_SHOVEL, 1, 1, 20), new RandomizedItemStack(Items.GOLDEN_HELMET, 1, 1, 20), new RandomizedItemStack(Items.GOLDEN_CHESTPLATE, 1, 1, 20), new RandomizedItemStack(Items.GOLDEN_LEGGINGS, 1, 1, 20), new RandomizedItemStack(Items.GOLDEN_BOOTS, 1, 1, 20)));
        PBECRegistry.register(new PBECMulti(new PBECRuinedPortal(new ILinear(5, 15), new ILinear(5, 10), new ILinear(2, 5), blockArray, loot), 80, new PBECConvertToNether(new DLinear(20.0, 40.0), new DGaussian(0.01, 0.15), "wastes"), 30, new PBECPool(new DLinear(5.0, 10.0), Blocks.LAVA, Collections.singletonList(new WeightedBlock(1, Blocks.AIR))), 0), "gateway_to_hell");
        PBECRegistry.register(new PBECHeightNoise(new DLinear(8.0, 30.0), new ILinear(-16, 16), new ILinear(1, 32), new ILinear(1, 8)), "height_noise");
        PBECRegistry.register(new PBECRandomShapes(new DLinear(40.0, 150.0), new DLinear(2.0, 5.0), new ILinear(3, 10), PandorasBoxHelper.blocks, new ZConstant(true)), "mad_geometry");
        PBECRegistry.register(new PBECRandomShapes(new DLinear(40.0, 150.0), new DLinear(1.0, 3.0), new ILinear(10, 80), PandorasBoxHelper.blocks, new ZConstant(false)), "madder_geometry");
        PBECRegistry.register(new PBECLavaCage(new DLinear(15.0, 40.0), Blocks.LAVA, null, Arrays.asList(new WeightedBlock(100, Blocks.IRON_BARS), new WeightedBlock(50, Blocks.BLACK_STAINED_GLASS_PANE)), Arrays.asList(new WeightedBlock(70, Blocks.OBSIDIAN), new WeightedBlock(10, Blocks.BEDROCK))), "lava_cage");
        PBECRegistry.register(new PBECLavaCage(new DLinear(15.0, 40.0), null, Blocks.WATER, Arrays.asList(new WeightedBlock(100, Blocks.GLASS_PANE), new WeightedBlock(50, Blocks.OAK_FENCE)), Arrays.asList(new WeightedBlock(70, Blocks.OBSIDIAN), new WeightedBlock(10, Blocks.BEDROCK))), "water_cage");
        PBECRegistry.register(new PBECCreativeTowers(new DLinear(5.0, 20.0), new ILinear(3, 10), PandorasBoxHelper.blocks), "classic");
        PBECRegistry.register(new PBECSpawnLightning(new ILinear(40, 200), new ILinear(6, 40), new DLinear(10.0, 40.0)), "lightning");
        PBECRegistry.register(new PBECConvertToDesert(new DLinear(10.0, 80.0)), "sand_for_dessert");
        PBECRegistry.register(new PBECConvertToEnd(new DLinear(10.0, 80.0)), "in_the_end");
        PBECRegistry.register(new PBECConvertToNether(new DLinear(10.0, 80.0), new DConstant(0), "wastes"), "hell_on_earth");
        PBECRegistry.register(new PBECConvertToNether(new DLinear(10.0, 80.0), new DConstant(0), "soul_sand_valley"), "valley_of_souls");
        PBECRegistry.register(new PBECConvertToNether(new DLinear(10.0, 80.0), new DConstant(0), "deltas"), "deltas_of_destruction");
        PBECRegistry.register(new PBECConvertToNether(new DLinear(10.0, 80.0), new DConstant(0), "warped"), "forest_of_calm");
        PBECRegistry.register(new PBECConvertToNether(new DLinear(10.0, 80.0), new DConstant(0), "crimson"), "forest_of_heat");
        PBECRegistry.register(new PBECConvertToLifeless(new DLinear(20.0, 80.0)), "lifeless");
        PBECRegistry.register(new PBECSpawnEntities(new ILinear(20, 100), new ILinear(20, 200), new IConstant(1), new IConstant(0), new IConstant(0), new IConstant(0), List.of(new WeightedEntity(100, "arrow", 1, 1)), new ValueThrow(new DLinear(0.05, 0.5), new DLinear(0.3, 0.8)), null), "trapped_tribe");
        PBECRegistry.register(new PBECBuffEntities(new ILinear(60, 20 * 30), new IWeighted(1, 100, 2, 80, 3, 50), new DLinear(8.0, 25.0), 0.2f, PandorasBoxHelper.debuffs), "buffed_down");
        PBECRegistry.register(new PBECBuffEntities(new ILinear(20 * 3, 20 * 6), new IConstant(1), new DLinear(8.0, 25.0), 0.95f, List.of(new WeightedPotion(100, MobEffects.MOVEMENT_SLOWDOWN, 4, 8, 20 * 6, 20 * 20))), "frozen_in_place");
        PBECRegistry.register(new PBECGenTrees(new DLinear(10.0, 80.0), new DLinear(1.0f / (32.0f * 32.0f * 32.0f), 1.0f / (6.0f * 6.0f * 6.0f)), new ZConstant(false), new IFlags(1, PBEffectGenTrees.treeSmall, 1.0, PBEffectGenTrees.treeNormal, 0.5, PBEffectGenTrees.treeBig, 0.5, PBEffectGenTrees.treeComplexNormal, 0.5, PBEffectGenTrees.treeTaiga, 0.5, PBEffectGenTrees.treeBirch, 0.5)), "flying_forest");
        PBECRegistry.register(new PBECCrushEntities(new ILinear(100, 300), new DLinear(20.0, 50.0)), "crush");
        PBECRegistry.register(new PBECBombentities(new ILinear(100, 400), new ILinear(5, 15), new DLinear(20.0, 50.0)), "bomberman");
        PBECRegistry.register(new PBECMulti(new PBECSpawnEntities(new ILinear(20, 100), new ILinear(4, 20), new IConstant(1), new IConstant(0), new IConstant(0), new IConstant(0), PandorasBoxHelper.mobs), 0, new PBECBuffEntities(new ILinear(60, 200), new IConstant(1), new DLinear(10.0, 40.0), 0.15f, List.of(new WeightedPotion(100, MobEffects.BLINDNESS, 1, 1, 20 * 40, 20 * 200))), 0), "pitch_black");
        PBECRegistry.register(new PBECCreateVoid(new ILinear(30, 60), new DLinear(20.0, 50.0)), "void");
        PBECRegistry.register(new PBECThrowItems(new ILinear(20, 200), new DLinear(20.0, 50.0), new DLinear(0.3, 0.7), new DLinear(0.0, 0.2), new ILinear(0, 5), PandorasBoxHelper.blocksAndItems), "are_these_mine");
        PBECRegistry.register(new PBECMulti(new PBECSetTime(new ILinear(60, 200), new ILinear(12000, 24000 * 5), new ZConstant(true)), 0, new PBECSetWeather(new IWeighted(0, 50, 1, 35, 2, 15), new ILinear(100, 12000), new ILinear(10, 120))), "time_lord");
        PBECRegistry.register(new PBECSpawnExploMobs(new ILinear(20, 60), new ILinear(8, 16), new ILinear(60, 200), new IWeighted(0, 100, 2, 100, 3, 100), PandorasBoxHelper.creatures), "explocreatures");
        PBECRegistry.register(new PBECSpawnExploMobs(new ILinear(20, 60), new ILinear(8, 16), new ILinear(60, 200), new IConstant(0), PandorasBoxHelper.mobs), "explomobs");
        PBECRegistry.register(new PBECMulti(new PBECDome(new ILinear(60, 200), new DLinear(10.0, 30.0), List.of(new WeightedBlock(100, Blocks.GLASS)), Blocks.WATER), 0, new PBECSpawnEntities(new ILinear(60, 100), new ILinear(4, 20), new IConstant(1), new IConstant(0), new IConstant(0), new IConstant(0), PandorasBoxHelper.waterCreatures), 100), "aquarium");
        PBECRegistry.register(new PBECMulti(new PBECDome(new ILinear(60, 200), new DLinear(10.0, 30.0), List.of(new WeightedBlock(100, Blocks.GLASS)), Blocks.WATER), 0, new PBECSpawnEntities(new ILinear(60, 100), new ILinear(1, 10), new IConstant(1), new IConstant(0), new IConstant(0), new IConstant(0), PandorasBoxHelper.waterMobs), 100), "aquaridoom");
        PBECRegistry.register(new PBECWorldSnake(new ILinear(160, 600), new DConstant(0.0), new DLinear(0.1, 0.5), new DLinear(0.5, 3.0), PandorasBoxHelper.blocks), "world_snake");
        PBECWorldSnake doubleSnakeGen = new PBECWorldSnake(new ILinear(160, 600), new DLinear(5.0, 20.0), new DLinear(0.1, 0.5), new DLinear(0.5, 3.0), PandorasBoxHelper.blocks);
        PBECRegistry.register(new PBECMulti(doubleSnakeGen, 0, doubleSnakeGen, 0), "double_snake");
        PBECWorldSnake doubleCaveSnake = new PBECWorldSnake(new ILinear(160, 600), new DLinear(5.0, 20.0), new DLinear(0.1, 0.5), new DLinear(1.0, 3.0), List.of(new WeightedBlock(100, Blocks.AIR)));
        PBECRegistry.register(new PBECMulti(doubleCaveSnake, 0, doubleCaveSnake, 0), "tunnel_bore");
        PBECRegistry.register(new PBECSpawnExplosions(new ILinear(80, 300), new ILinear(6, 40), new DLinear(10.0, 40.0), new DLinear(1.0, 4.0), new ZChance(0.3), new ZConstant(true)), "creeper_soul");
        PBECRegistry.register(new PBECBombpack(new DLinear(10.0, 60.0), new ILinear(60, 200)), "bombpack");
        PBECRegistry.register(new PBECCover(new DLinear(10.0, 15.0), new ZConstant(false), List.of(new WeightedBlock(100, Blocks.AIR))), "make_thin");
        PBECRegistry.register(new PBECCover(new DLinear(10.0, 15.0), new ZChance(0.5), PandorasBoxHelper.blocks), "cover");
        PBECRegistry.register(new PBECTargets(new ILinear(40, 100), new DLinear(10.0, 50.0), new DLinear(6.0, 16.0), new DLinear(0.2, 0.5), PandorasBoxHelper.mobs), "target");
        PBECRegistry.register(new PBECSpawnArmy(new ILinear(1, 3), new ILinear(0, 5), Arrays.asList(new WeightedEntity(100, "zombie", 5, 10), new WeightedEntity(80, "skeleton", 4, 7), new WeightedEntity(40, "wither_skeleton", 5, 10))), "armored_army");
        PBECRegistry.register(new PBECSpawnArmy(new ILinear(1, 3), new IConstant(0), PandorasBoxHelper.mobs), "army");
        PBECRegistry.register(new PBECSpawnEntities(new ILinear(20, 40), new IWeighted(1, 100, 2, 20, 3, 5), new IConstant(1), new ILinear(2, 6), new ILinear(2, 10), new IConstant(1), PandorasBoxHelper.mobs), "boss");
        PBECRegistry.register(new PBECConvertToIce(new DLinear(10.0, 80.0)), "ice_age");
        PBECRegistry.register(new PBECTeleportEntities(0.1f, new ILinear(5, 100), new DLinear(10.0, 80.0), new DLinear(10.0, 100.0), new IConstant(1)), "telerandom");
        PBECRegistry.register(new PBECTeleportEntities(0.5f, new ILinear(5, 200), new DLinear(10.0, 80.0), new DLinear(0.5, 5.0), new ILinear(5, 20)), "crazyport");
        PBECRegistry.register(new PBECExplosion(new ILinear(50, 120), new DGaussian(2.0, 8.0), new ZChance(0.3), Level.ExplosionInteraction.TNT), "thing_go_boom");
        PBECRegistry.register(new PBECMulti(new PBECExplosion(new ILinear(80, 100), new DConstant(15.0), new ZConstant(true), Level.ExplosionInteraction.BLOCK), 0, new PBECConvertToLifeless(new DGaussian(20.0, 40.0)), 100, new PBECSpawnEntities(new ILinear(20, 100), new ILinear(4, 20), new IConstant(1), new IConstant(0), new IConstant(0), new IConstant(0), PandorasBoxHelper.mobs), 100), "apocalyptic_boom");
        PBECRegistry.register(new PBECSpawnBlocks(new ILinear(50, 120), new ILinear(0, 2), Arrays.asList(new WeightedBlock(1, Blocks.TNT), new WeightedBlock(1, Blocks.REDSTONE_BLOCK)), null, new ValueSpawn(new DLinear(5.0, 10.0), new DConstant(150.0))).setShuffleBlocks(false), "danger_call");

        PBECRegistry.register(new PBECSpawnEntities(new ILinear(20, 100), new ILinear(4, 20), new IConstant(1), new IWeighted(0, 100, 2, 50, 3, 30), new IConstant(0), new IConstant(0), PandorasBoxHelper.creatures), "animals");
        PBECRegistry.register(new PBECSpawnEntities(new ILinear(20, 100), new ILinear(2, 6), new ILinear(2, 5), new IWeighted(0, 100, 2, 35, 3, 20), new IConstant(0), new IConstant(0), PandorasBoxHelper.creatures), "animal_towers");
        PBECRegistry.register(new PBECSpawnEntities(new ILinear(20, 100), new ILinear(1, 8), new IConstant(1), new IWeighted(0, 50, 2, 100, 3, 20), new IConstant(0), new IConstant(0), PandorasBoxHelper.tameableCreatures), "tamer");
        PBECRegistry.register(new PBECSpawnItems(new ILinear(1, 5), new ILinear(0, 30), PandorasBoxHelper.blocksAndItems), "items");
        PBECRegistry.register(new PBECSpawnEnchantedItems(new ILinear(1, 2), new ILinear(0, 30), new IExp(1, 30, 10.0), PandorasBoxHelper.enchantableArmorList, new ZChance(0.7)), "epic_armor");
        PBECRegistry.register(new PBECSpawnEnchantedItems(new ILinear(1, 2), new ILinear(0, 30), new IExp(1, 30, 10.0), PandorasBoxHelper.enchantableToolList, new ZChance(0.7)), "epic_tool");
        PBECRegistry.register(new PBECSpawnEnchantedItems(new ILinear(1, 2), new ILinear(0, 30), new IExp(1, 30, 10.0), PandorasBoxHelper.items, new ZChance(0.7)), "epic_thing");
        PBECRegistry.register(new PBECSpawnEnchantedItems(new ILinear(1, 4), new ILinear(0, 30), new IExp(1, 30, 10.0), List.of(new RandomizedItemStack(Items.ENCHANTED_BOOK, 1, 1, 100)), new ZChance(0.1)), "enchanted_book");
        PBECRegistry.register(new PBECSpawnManySameItems(new ILinear(0, 30), PandorasBoxHelper.blocksAndItems), "resources");
        PBECRegistry.register(new PBECSpawnItemSet(new ILinear(0, 30), PandorasBoxHelper.equipmentSets), "equipment_set");
        PBECRegistry.register(new PBECSpawnManySameItems(new ILinear(0, 30), PandorasBoxHelper.blocksAndItems, null, PBECSpawnItems.defaultShowerSpawn()), "item_rain");
        PBECRegistry.register(new PBECSpawnEntities(new ILinear(20, 100), new ILinear(10, 30), new IConstant(1), new IConstant(0), new IConstant(0), new IConstant(0), PandorasBoxHelper.creatures, null, new ValueSpawn(new DLinear(5.0, 20.0), new DConstant(150.0))), "dead_creatures");
        PBECRegistry.register(new PBECSpawnEntities(new ILinear(20, 100), new ILinear(10, 30), new IConstant(1), new IConstant(0), new IConstant(0), new IConstant(0), PandorasBoxHelper.landMobs, null, new ValueSpawn(new DLinear(5.0, 20.0), new DConstant(150.0))), "dead_mobs");
        PBECRegistry.register(new PBECSpawnEntities(new ILinear(20, 100), new ILinear(10, 110), new IConstant(1), new IConstant(0), new IConstant(0), new IConstant(0), List.of(new WeightedEntity(100, "pbspecial_experience", 1, 1))), "experience");
        PBECRegistry.register(new PBECGenTrees(new DLinear(10.0, 80.0), new DLinear(1.0f / (8.0f * 8.0f), 1.0f / (2.0f * 2.0f)), new ZConstant(true), new IFlags(1, PBEffectGenTrees.treeSmall, 0.5, PBEffectGenTrees.treeNormal, 0.5, PBEffectGenTrees.treeBig, 0.5, PBEffectGenTrees.treeComplexNormal, 0.5, PBEffectGenTrees.treeTaiga, 0.5, PBEffectGenTrees.treeBirch, 0.5)), "sudden_forest");
        PBECRegistry.register(new PBECGenTrees(new DLinear(10.0, 80.0), new DLinear(1.0f / (8.0f * 8.0f), 1.0f / (3.0f * 3.0f)), new ZConstant(true), new IFlags(1, PBEffectGenTrees.treeJungle, 0.7, PBEffectGenTrees.treeHuge, 0.7, PBEffectGenTrees.treeComplexNormal, 0.7)), "sudden_jungle");
        PBECRegistry.register(new PBECGenTreesOdd(new DLinear(10.0, 80.0), new DLinear(1.0f / (8.0f * 8.0f), 1.0f / (3.0f * 3.0f)), new ZConstant(true), new IFlags(1, PBEffectGenTreesOdd.treeJungle, 0.7), PandorasBoxHelper.blocks, PandorasBoxHelper.blocks), "odd_jungle");
        PBECRegistry.register(new PBECBuffEntities(new ILinear(60, 20 * 30), new IWeighted(1, 100, 2, 80, 3, 50), new DLinear(8.0, 25.0), 0.2f, PandorasBoxHelper.buffs), "buffed_up");
        PBECRegistry.register(new PBECConvertToSnow(new DLinear(10.0, 80.0)), "snow_age");
        PBECRegistry.register(new PBECConvertToOverworld(new DLinear(10.0, 80.0)), "normal_land");
        PBECRegistry.register(new PBECConvertToHFT(new DLinear(10.0, 80.0)), "happy_fun_times");
        PBECRegistry.register(new PBECConvertToMushroom(new DLinear(10.0, 80.0)), "shroomify");
        PBECRegistry.register(new PBECConvertToHomo(new DLinear(10.0, 80.0)), "rainbows");
        PBECRegistry.register(new PBECConvertToRainbowCloth(new DLinear(10.0, 80.0), new ILinear(2, 10), new DLinear(0.2, 4.0)), "all_rainbow");
        PBECRegistry.register(new PBECConvertToFarm(new DLinear(5.0, 15.0), new DLinear(0.01, 0.5)), "farm");
        PBECRegistry.register(new PBECConvertToCity(new DLinear(40.0, 100.0), PandorasBoxHelper.landMobs), "cityscape");
        PBECRegistry.register(new PBECMulti(new PBECConvertToHeavenly(new DLinear(20.0, 80.0)), 0, new PBECRandomShapes(new DLinear(20.0, 80.0), new DLinear(2, 5), new ILinear(8, 10), List.of(new WeightedBlock(1, Blocks.COBWEB)), new ZConstant(true)), 0), "heavenly");
        PBECRegistry.register(new PBECMulti(new PBECConvertToHalloween(new DLinear(10.0, 80.0)), 0, new PBECSetTime(new ILinear(60, 120), new ILinear(15000, 20000), new ZConstant(false)), 0), "halloween");
        PBECRegistry.register(new PBECMulti(new PBECConvertToChristmas(new DLinear(10.0, 80.0)), 0, new PBECSetTime(new ILinear(60, 120), new ILinear(15000, 20000), new ZConstant(false)), 0), "christmas");
        PBECRegistry.register(new PBECSpawnEntities(new ILinear(100, 200), new ILinear(10, 30), new IConstant(1), new IConstant(0), new IConstant(0), new IConstant(0), List.of(new WeightedEntity(100, "pbspecial_fireworks", 1, 1)), new ValueThrow(new DLinear(0.02, 0.06), new DConstant(0.01)), new ValueSpawn(new DLinear(2.0, 20.0), new DConstant(0.0))), "new_year");
        PBECRegistry.register(new PBECSpawnBlocks(new ILinear(6, 20), new ILinear(5, 20), PandorasBoxHelper.blocks, null, new ValueSpawn(new DConstant(0.0), new DConstant(25.0))), "block_tower");
        PBECRegistry.register(new PBECMulti(new PBECDome(new ILinear(60, 200), new DLinear(10.0, 30.0), List.of(new WeightedBlock(100, Blocks.GLASS)), null), 0, new PBECSpawnEntities(new ILinear(60, 100), new ILinear(4, 20), new IConstant(1), new IConstant(0), new IConstant(0), new IConstant(0), PandorasBoxHelper.creatures), 0), "terrarium");
        PBECRegistry.register(new PBECSpawnArmy(new ILinear(1, 3), new IConstant(0), PandorasBoxHelper.creatures), "animal_farm");
    }
}
