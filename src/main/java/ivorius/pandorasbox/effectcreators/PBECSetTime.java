/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectSetTime;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ZValue;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECSetTime implements PBEffectCreator
{
    public IValue time;
    public IValue worldTime;
    public ZValue add;

    public PBECSetTime(IValue time, IValue worldTime, ZValue add)
    {
        this.time = time;
        this.worldTime = worldTime;
        this.add = add;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random)
    {
        int time = this.time.getValue(random);
        int worldTime = this.worldTime.getValue(random);
        boolean add = this.add.getValue(random);

        if (!add) {
            int currentTime = (int) (world.getGameTime() % 24000);
            return new PBEffectSetTime(time, worldTime - currentTime);
        }
        else return new PBEffectSetTime(time, worldTime);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.7f;
    }
}
