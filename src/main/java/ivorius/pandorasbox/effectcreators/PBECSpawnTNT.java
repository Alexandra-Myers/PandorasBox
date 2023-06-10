/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ValueSpawn;
import ivorius.pandorasbox.random.ValueThrow;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECSpawnTNT implements PBEffectCreator
{
    public IValue time;
    public IValue number;
    public IValue fuseTime;

    public ValueThrow valueThrow;
    public ValueSpawn valueSpawn;

    public PBECSpawnTNT(IValue time, IValue number, IValue fuseTime, ValueThrow valueThrow, ValueSpawn valueSpawn)
    {
        this.time = time;
        this.number = number;
        this.fuseTime = fuseTime;
        this.valueThrow = valueThrow;
        this.valueSpawn = valueSpawn;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        int time = this.time.getValue(random);
        int number = this.number.getValue(random);

        String[][] entitiesToSpawn = new String[number][];
        for (int i = 0; i < number; i++)
        {
            entitiesToSpawn[i] = new String[]{"pbspecial_tnt" + this.fuseTime.getValue(random)};
        }

        return PBECSpawnEntities.constructEffect(random, entitiesToSpawn, time, valueThrow, valueSpawn);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.15f;
    }
}
