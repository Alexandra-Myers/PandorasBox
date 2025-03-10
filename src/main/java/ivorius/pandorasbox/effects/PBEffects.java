package ivorius.pandorasbox.effects;

import com.mojang.datafixers.util.Either;
import ivorius.pandorasbox.PandorasBox;
import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effectcreators.*;
import ivorius.pandorasbox.effects.generate.NetherBiome;
import ivorius.pandorasbox.random.*;
import ivorius.pandorasbox.utils.EitherArrayList;
import ivorius.pandorasbox.weighted.WeightedBlock;
import ivorius.pandorasbox.weighted.WeightedEntity;
import ivorius.pandorasbox.weighted.WeightedTag;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.world.level.block.Blocks;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by lukas on 01.12.14.
 */
public class PBEffects {
    public static PBEffectCreator[] MELTDOWN_CREATORS = new PBEffectCreator[] {
            new PBECSpawnLightning(new ILinear(80, 150), new ILinear(10, 48), new DLinear(10.0, 30.0))
    };
    public static void registerEffectCreators() {
        PandorasBoxHelper.initialize();

        MELTDOWN_CREATORS = new PBEffectCreator[] {
                new PBECSpawnLightning(new ILinear(80, 150), new ILinear(10, 48), new DLinear(10.0, 30.0))
        };

        addAllToMeltdown(new PBECSpawnBlocks(new ILinear(10, 40), new ILinear(3, 5), new ZConstant(true), PandorasBoxHelper.blocks, Optional.empty(), Optional.of(PBECSpawnBlocks.defaultShowerSpawn())),
                new PBECSpawnBlocks(new ILinear(10, 25), new ILinear(3, 5), new ZConstant(true), PandorasBoxHelper.heavyBlocks, Optional.empty(), Optional.of(PBECSpawnBlocks.defaultShowerSpawn())),
                new PBECSpawnBlocks(new ILinear(6, 20), new ILinear(2, 5), new ZConstant(true), PandorasBoxHelper.blocks, Optional.empty(), Optional.of(new ValueSpawn(new DConstant(0.0), new DConstant(25.0)))),
                new PBECConvertGeneric(new DLinear(10.0, 15.0), 0.1F, PBECConversions.END),
                new PBECConvertToNether(new DLinear(10.0, 15.0), new DLinear(0.0, 0.3), NetherBiome.NETHER_WASTES),
                new PBECConvertToNether(new DLinear(10.0, 15.0), new DLinear(0.0, 0.3), NetherBiome.SOUL_SAND_VALLEY),
                new PBECConvertToNether(new DLinear(10.0, 15.0), new DLinear(0.0, 0.3), NetherBiome.WARPED_FOREST),
                new PBECConvertToNether(new DLinear(10.0, 15.0), new DLinear(0.0, 0.3), NetherBiome.CRIMSON_FOREST),
                new PBECWorldSnake(new ILinear(40, 300), new DLinear(5.0, 10.0), new DLinear(0.6, 1.0), new DLinear(1.0, 3.0), PandorasBoxHelper.blocks),
                new PBECConvertToCity(new DLinear(10.0, 20.0), PandorasBoxHelper.landMobs.stream().filter(weightedEntity -> !weightedEntity.entityID().startsWith("pbspecial")).toList()),
                PBECMulti.create(new PBECConvertGeneric(new DLinear(10.0, 15.0), 0.1F, PBECConversions.HALLOWEEN), 0, new PBECSetTime(new ILinear(30, 60), new ILinear(15000, 20000), new ZConstant(false)), 0),
                PBECMulti.create(new PBECConvertGeneric(new DLinear(10.0, 15.0), 0.1F, PBECConversions.CHRISTMAS), 0, new PBECSetTime(new ILinear(30, 60), new ILinear(15000, 20000), new ZConstant(false)), 0),
                new PBECCrushEntities(new ILinear(50, 125), new DLinear(20.0, 50.0), new ZConstant(true), new ILinear(1, 5)),
                new PBECHeightNoise(new DLinear(5.0, 13.0), new ILinear(-16, 16), new ILinear(1, 32), new ILinear(1, 4)),
                new PBECSpawnTNT(new ILinear(10, 30), new ILinear(15, 50), new ILinear(20, 1000), new ZConstant(true), new ValueSpawn(new DLinear(5.0, 50.0), new DConstant(0.0)), new ValueThrow(new DLinear(0.2, 2.0), new DLinear(0.5, 5.0))),
                PBECMulti.create(new PBECSetTime(new ILinear(10, 20), new ILinear(12000, 24000 * 5), new ZConstant(true)), 0, new PBECSetWeather(new IWeighted(0, 50, 1, 15, 2, 35), new ILinear(100, 12000), new ILinear(10, 15)), 0),
                new PBECSpawnEntities(new ILinear(5, 10), new IWeighted(2, 100, 3, 20, 4, 5), new IConstant(1), new ILinear(2, 6), new ILinear(2, 10), new IConstant(1), new ZConstant(true), PandorasBoxHelper.mobs),
                new PBECSpawnItemSet(new ILinear(2, 20), new ZConstant(true), PandorasBoxHelper.equipmentSets),
                PBECMulti.create(new PBECSpawnEnchantedItems(new IConstant(1), new ILinear(2, 15), new IExp(3, 30, 10.0), PandorasBoxHelper.enchantableArmorList, new ZChance(0.8), new ZConstant(true)), 0, new PBECSpawnEnchantedItems(new IConstant(1), new ILinear(2, 15), new IExp(3, 30, 10.0), PandorasBoxHelper.enchantableToolList, new ZChance(0.8), new ZConstant(true)), 0),
                new PBECSpawnEntities(new ILinear(10, 50), new ILinear(10, 110), new IConstant(1), new IConstant(0), new IConstant(0), new IConstant(0), new ZConstant(true), List.of(new WeightedEntity(100, "pbspecial_experience", 1, 1))),
                new PBECSpawnEntities(new ILinear(10, 100), new ILinear(10, 30), new IConstant(1), new IConstant(0), new IConstant(0), new IConstant(0), new ZConstant(true), List.of(new WeightedEntity(100, "pbspecial_fireworks", 1, 1)), Optional.of(new ValueThrow(new DLinear(0.02, 0.06), new DConstant(0.01))), Optional.of(new ValueSpawn(new DLinear(2.0, 20.0), new DConstant(0.0)))),
                new PBECSpawnExplosions(new ILinear(10, 150), new ILinear(6, 20), new DLinear(10.0, 20.0), new DLinear(3.0, 5.0), new ZChance(0.3), new ZConstant(true)),
                new PBECConvertToHFT(new DLinear(10.0, 15.0), PandorasBox.ALL_TERRACOTTA),
                PBECMulti.create(new PBECConvertGeneric(new DLinear(10.0, 15.0), 0.1F, PBECConversions.HOMO), 10, new PBECSpawnArmy(new ILinear(1, 4), new IConstant(0), new ZConstant(true), PandorasBoxHelper.creatures), 0),
                PBECMulti.create(new PBECConvertGeneric(new DLinear(10.0, 15.0), 0.1F, PBECConversions.ICE), 20, new PBECDome(new ILinear(5, 10), new DGaussian(10.0, 15.0), new EitherArrayList<>(Arrays.asList(Either.left(new WeightedBlock(100, Blocks.PACKED_ICE)), Either.left(new WeightedBlock(80, Blocks.ICE)), Either.left(new WeightedBlock(50, Blocks.BLUE_ICE)))), Optional.of(Blocks.WATER)), 0),
                new PBECLavaCage(new DGaussian(10.0, 20.0), Optional.of(Blocks.LAVA), Optional.empty(), new EitherArrayList<>(Collections.singletonList(Either.left(new WeightedBlock(100, Blocks.NETHER_BRICK_WALL)))), new EitherArrayList<>(Collections.singletonList(Either.right(new WeightedTag<>(100, ConventionalBlockTags.CRYING_OBSIDIANS))))),
                new PBECBuffEntities(new ILinear(30, 150), new IWeighted(1, 100, 2, 80, 3, 50), new DLinear(8.0, 10.0), 0.0f, PandorasBoxHelper.buffs),
                PBECMulti.create(new PBECConvertGeneric(new DLinear(10.0, 15.0), 0.1F, PBECConversions.DESERT), 0, new PBECCreativeTowers(new DLinear(10.0, 15.0), new ILinear(4, 10), new EitherArrayList<>(Arrays.asList(Either.right(new WeightedTag<>(100, ConventionalBlockTags.DYED)), Either.right(new WeightedTag<>(50, ConventionalBlockTags.CHAINS))))), 0),
                PBECMulti.create(new PBECTransform(new DLinear(10.0, 15.0), PandorasBoxHelper.blocks), 0, new PBECGenTrees(new DGaussian(10.0, 20.0), new DLinear(1.0f / (32.0f * 32.0f * 32.0f), 1.0f / (6.0f * 6.0f * 6.0f)), new ZConstant(false), new IFlags(1, 0, 1.0, 1, 0.5, 2, 0.5, 5, 0.5, 6, 0.5, 7, 0.5)), 20),
                PBECMulti.create(new PBECConvertGeneric(new DLinear(10.0, 15.0), 0.1F, PBECConversions.HEAVENLY), 0, new PBECSpawnItemSet(new ILinear(1, 5), new ZConstant(true), PandorasBoxHelper.equipmentSets), 0, new PBECBombpack(new DLinear(10.0, 25.0), new ILinear(15, 100)), 0),
                PBECMulti.create(new PBECConvertGeneric(new DLinear(10.0, 15.0), 0.1F, PBECConversions.LIFELESS), 0, new PBECRandomShapes(new DLinear(10.0, 20.0), new DLinear(2, 5), new ILinear(8, 13), new EitherArrayList<>(List.of(Either.right(new WeightedTag<>(1, ConventionalBlockTags.STONES)), Either.right(new WeightedTag<>(5, ConventionalBlockTags.COBBLESTONES)))), new ZConstant(true)), 0),
                new PBECConvertGeneric(new DLinear(10.0, 15.0), 0.1F, PBECConversions.OVERWORLD),
                PBECMulti.create(new PBECConvertGeneric(new DLinear(10.0, 20.0), 0.1F, PBECConversions.MUSHROOM), 0, new PBECSpawnEntities(new ILinear(10, 50), new ILinear(3, 10), new ILinear(2, 5), new IConstant(0), new IConstant(0), new ILinear(0, 1), new ZConstant(true), PandorasBoxHelper.creatures), 10),
                new PBECGenTreesOdd(new DGaussian(10.0, 20.0), new DLinear(1.0f / (8.0f * 8.0f), 1.0f / (3.0f * 3.0f)), new ZChance(0.50), new IFlags(1, 0, 0.7), PandorasBoxHelper.blocks, PandorasBoxHelper.blocks));
    }

    public static void addMeltdownCreator(PBEffectCreator pbEffectCreator) {
        int length = MELTDOWN_CREATORS.length;
        MELTDOWN_CREATORS = Arrays.copyOf(MELTDOWN_CREATORS, length + 1);
        MELTDOWN_CREATORS[length] = pbEffectCreator;
    }

    public static void addAllToMeltdown(PBEffectCreator... pbEffectCreators) {
        int length = MELTDOWN_CREATORS.length;
        MELTDOWN_CREATORS = Arrays.copyOf(MELTDOWN_CREATORS, length + pbEffectCreators.length);
        int i = 0;
        for (; length < MELTDOWN_CREATORS.length; length++) {
            if (i > pbEffectCreators.length) break;
            MELTDOWN_CREATORS[length] = pbEffectCreators[i];
            i++;
        }
    }
}
