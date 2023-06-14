/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import ivorius.pandorasbox.utils.ArrayListExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.data.worldgen.features.TreeFeatures.*;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenTrees extends PBEffectGenerateByGenerator
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

    private ArrayListExtensions<ResourceKey<ConfiguredFeature<?, ?>>> treeGens;

    public PBEffectGenTrees(int time, double range, int unifiedSeed, boolean requiresSolidGround, double chancePerBlock, int generatorFlags)
    {
        super(time, range, unifiedSeed, requiresSolidGround, chancePerBlock, generatorFlags);
        initializeGens();
    }

    private void initializeGens()
    {
        treeGens = new ArrayListExtensions<>();

        treeGens.add(JUNGLE_TREE);
        treeGens.add(OAK);
        treeGens.add(FANCY_OAK);
        treeGens.add(MEGA_JUNGLE_TREE);
        treeGens.add(JUNGLE_TREE);
        treeGens.add(DARK_OAK);
        treeGens.add(SPRUCE);
        treeGens.add(BIRCH);
    }

    @Override
    public List<?> getGenerators()
    {
        return treeGens;
    }
}
