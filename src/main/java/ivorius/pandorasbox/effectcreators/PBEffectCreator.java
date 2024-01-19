/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.effects.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Created by lukas on 30.03.14.
 */
public interface PBEffectCreator
{
    default PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        return new PBEffectDuplicateBox(0);
    }

    float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random);

}
