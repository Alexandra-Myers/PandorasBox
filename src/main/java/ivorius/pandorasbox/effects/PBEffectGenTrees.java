/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.utils.ArrayListExtensions;
import ivorius.pandorasbox.worldgen.MegaTreeFeature;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.List;

import static net.minecraft.data.worldgen.features.TreeFeatures.*;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenTrees extends PBEffectGenerateByGenerator<ResourceKey<ConfiguredFeature<?, ?>>>
{
    public PBEffectGenTrees() {}

    public static final int treeSmall = 0;
    public static final int treeNormal = 1;
    public static final int treeBig = 2;
    public static final int treeHuge = 3;
    public static final int treeJungle = 4;
    public static final int treeComplexNormal = 5;
    public static final int treeTaiga = 6;
    public static final int treeBirch = 7;

    public PBEffectGenTrees(int time, double range, int unifiedSeed, boolean requiresSolidGround, double chancePerBlock, int generatorFlags)
    {
        super(time, range, unifiedSeed, requiresSolidGround, chancePerBlock, generatorFlags);
        treeGens = initializeGens();
    }

    @Override
    public ArrayListExtensions<ResourceKey<ConfiguredFeature<?, ?>>> initializeGens()
    {
        ArrayListExtensions<ResourceKey<ConfiguredFeature<?, ?>>> trees = new ArrayListExtensions<>();
        trees.add(treeSmall, JUNGLE_BUSH);
        trees.add(treeNormal, OAK);
        trees.add(treeBig, FANCY_OAK);
        trees.add(treeHuge, MEGA_JUNGLE_TREE);
        trees.add(treeJungle, JUNGLE_TREE);
        trees.add(treeComplexNormal, DARK_OAK);
        trees.add(treeTaiga, SPRUCE);
        trees.add(treeBirch, BIRCH);
        return trees;
    }

    @Override
    public List<ResourceKey<ConfiguredFeature<?, ?>>> getGenerators()
    {
        return treeGens;
    }
}
