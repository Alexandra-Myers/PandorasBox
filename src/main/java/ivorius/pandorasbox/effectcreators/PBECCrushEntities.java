/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectEntitiesCrush;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECCrushEntities implements PBEffectCreator {
    public IValue time;
    public DValue range;

    public PBECCrushEntities(IValue time, DValue range) {
        this.time = time;
        this.range = range;
    }

    @Override
    public PBEffect constructEffect(World world, double x, double y, double z, Random random) {
        int cycles = 1;
        if (random.nextBoolean())
            cycles += random.nextInt(5) + 1;

        int time = this.time.getValue(random);
        double range = this.range.getValue(random);
        double strength = (0.15 + random.nextDouble() * 0.15) * (1 + (cycles - 1) * 0.3);

        return new PBEffectEntitiesCrush(time, range, cycles, strength);
    }

    @Override
    public float chanceForMoreEffects(World world, double x, double y, double z, Random random) {
        return 0.15f;
    }
}
