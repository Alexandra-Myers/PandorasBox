/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import net.minecraft.world.gen.feature.*;

/**
 * Created by lukas on 30.03.14.
 */
public class PBEffectGenTrees extends PBEffectGenerateByGenerator
{

    public static final int treeSmall = 0;
    public static final int treeNormal = 1;
    public static final int treeBig = 2;
    public static final int treeHuge = 3;
    public static final int treeJungle = 4;
    public static final int treeComplexNormal = 5;
    public static final int treeTaiga = 6;
    public static final int treeBirch = 7;

    private ConfiguredFeature[] treeGens;

    public PBEffectGenTrees(int time, double range, int unifiedSeed, boolean requiresSolidGround, double chancePerBlock, int generatorFlags)
    {
        super(time, range, unifiedSeed, requiresSolidGround, chancePerBlock, generatorFlags);
        initializeGens();
    }

    private void initializeGens()
    {
        treeGens = new ConfiguredFeature[8];

        treeGens[treeSmall] = Features.JUNGLE_BUSH;
        treeGens[treeNormal] = Features.OAK;
        treeGens[treeBig] = Features.FANCY_OAK;
        treeGens[treeHuge] = Features.MEGA_JUNGLE_TREE;
        treeGens[treeJungle] = Features.JUNGLE_TREE;
        treeGens[treeComplexNormal] = Features.DARK_OAK;
        treeGens[treeTaiga] = Features.SPRUCE;
        treeGens[treeBirch] = Features.BIRCH;
    }

    @Override
    public ConfiguredFeature[] getGenerators()
    {
        return treeGens;
    }
}
