/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ValueSpawn;
import ivorius.pandorasbox.random.ValueThrow;
import ivorius.pandorasbox.weighted.WeightedEntity;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import java.util.Collection;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECSpawnExploMobs implements PBEffectCreator
{
    public IValue time;
    public IValue number;
    public IValue fuseTime;
    public IValue nameEntities;
    public Collection<WeightedEntity> entityIDs;

    public ValueThrow valueThrow;
    public ValueSpawn valueSpawn;

    public PBECSpawnExploMobs(IValue time, IValue number, IValue fuseTime, IValue nameEntities, Collection<WeightedEntity> entityIDs, ValueThrow valueThrow, ValueSpawn valueSpawn)
    {
        this.time = time;
        this.number = number;
        this.fuseTime = fuseTime;
        this.nameEntities = nameEntities;
        this.entityIDs = entityIDs;
        this.valueThrow = valueThrow;
        this.valueSpawn = valueSpawn;
    }

    public PBECSpawnExploMobs(IValue time, IValue number, IValue fuseTime, IValue nameEntities, Collection<WeightedEntity> entityIDs)
    {
        this(time, number, fuseTime, nameEntities, entityIDs, PBECSpawnEntities.defaultThrow(), PBECSpawnEntities.defaultSpawn());
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        int time = this.time.getValue(random);
        int number = this.number.getValue(random);
        WeightedEntity entity = PandorasBoxHelper.getRandomEntityFromList(random, entityIDs);
        boolean invisible = random.nextBoolean();

        String[][] entitiesToSpawn = new String[number][];
        for (int i = 0; i < number; i++)
        {
            entitiesToSpawn[i] = new String[2];
            entitiesToSpawn[i][0] = (invisible ? "pbspecial_invisible_tnt" : "pbspecial_tnt") + this.fuseTime.getValue(random);
            entitiesToSpawn[i][1] = entity.entityID;
        }

        int nameEntities = this.nameEntities.getValue(random);

        return PBECSpawnEntities.constructEffect(random, entitiesToSpawn, time, nameEntities, 0, 0, valueThrow, valueSpawn);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }
}
