/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

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

    private ConfiguredFeature[] treeGens;

    public PBEffectGenTrees(int time, double range, int unifiedSeed, boolean requiresSolidGround, double chancePerBlock, int generatorFlags)
    {
        super(time, range, unifiedSeed, requiresSolidGround, chancePerBlock, generatorFlags);
    }

    @Override
    public void generateOnBlock(Level world, PandorasBoxEntity entity, Vec3d effectCenter, RandomSource random, int pass, BlockPos pos, double range) {
        initializeGens(world);
        super.generateOnBlock(world, entity, effectCenter, random, pass, pos, range);
    }

    private void initializeGens(Level world)
    {
        treeGens = new ConfiguredFeature[8];
        Registry<ConfiguredFeature<?, ?>> configuredFeatureRegistry = world.registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE);

        treeGens[treeSmall] = configuredFeatureRegistry.get(JUNGLE_BUSH);
        treeGens[treeNormal] = configuredFeatureRegistry.get(OAK);
        treeGens[treeBig] = configuredFeatureRegistry.get(FANCY_OAK);
        treeGens[treeHuge] = configuredFeatureRegistry.get(MEGA_JUNGLE_TREE);
        treeGens[treeJungle] = configuredFeatureRegistry.get(JUNGLE_TREE);
        treeGens[treeComplexNormal] = configuredFeatureRegistry.get(DARK_OAK);
        treeGens[treeTaiga] = configuredFeatureRegistry.get(SPRUCE);
        treeGens[treeBirch] = configuredFeatureRegistry.get(BIRCH);
    }

    @Override
    public ConfiguredFeature[] getGenerators()
    {
        return treeGens;
    }
}
