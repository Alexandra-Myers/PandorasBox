/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectMulti;
import ivorius.pandorasbox.random.*;
import ivorius.pandorasbox.weighted.WeightedEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECSpawnArmy implements PBEffectCreator
{
    public IValue groups;
    public IValue equipLevel;
    public ZValue spawnFromEffectCenter;

    public Collection<WeightedEntity> entityIDs;

    public ValueThrow valueThrow;
    public ValueSpawn valueSpawn;

    public PBECSpawnArmy(IValue groups, IValue equipLevel, ZValue spawnFromEffectCenter, Collection<WeightedEntity> entityIDs, ValueThrow valueThrow, ValueSpawn valueSpawn)
    {
        this.groups = groups;
        this.equipLevel = equipLevel;
        this.spawnFromEffectCenter = spawnFromEffectCenter;
        this.entityIDs = entityIDs;
        this.valueThrow = valueThrow;
        this.valueSpawn = valueSpawn;
    }

    public PBECSpawnArmy(IValue groups, IValue equipLevel, ZValue spawnFromEffectCenter, Collection<WeightedEntity> entityIDs)
    {
        this(groups, equipLevel, spawnFromEffectCenter, entityIDs, PBECSpawnEntities.defaultThrow(), PBECSpawnEntities.defaultSpawn());
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        int groups = this.groups.getValue(random);

        WeightedEntity[] entitySelection = PandorasBoxHelper.getRandomEntityList(random, entityIDs);

        PBEffect[] effects = new PBEffect[groups * 2];
        int[] delays = new int[effects.length];

        for (int i = 0; i < groups; i++)
        {
            WeightedEntity soldierType = entitySelection[random.nextInt(entitySelection.length)];
            String[][] soldiers = new String[new ILinear(soldierType.minNumber, soldierType.maxNumber).getValue(random)][];
            Arrays.fill(soldiers, new String[]{soldierType.entityID});

            int equipLevel = this.equipLevel.getValue(random);
            int equipLevelCaptain = 5 + equipLevel;
            int buffLevelCaptain = random.nextInt(5);

            effects[i * 2] = PBECSpawnEntities.constructEffect(random, soldiers, 50, 0, equipLevel, 0, spawnFromEffectCenter, valueThrow, valueSpawn);
            effects[i * 2 + 1] = PBECSpawnEntities.constructEffect(random, new String[][]{new String[]{soldierType.entityID}}, 25, 1, equipLevelCaptain, buffLevelCaptain, spawnFromEffectCenter, valueThrow, valueSpawn);
            delays[i * 2] = i * 50;
            delays[i * 2 + 1] = i * 50 + 25;
        }

        return new PBEffectMulti(effects, delays);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }
}
