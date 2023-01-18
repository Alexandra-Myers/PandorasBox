/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.*;
import ivorius.pandorasbox.entitites.EntityPandorasBox;
import net.minecraft.block.Block;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by lukas on 30.03.14.
 */
public interface PBEffectCreator
{
    default PBEffect constructEffect(World world, double x, double y, double z, Random random) {

        return new PBEffectDuplicateBox(0);
    }

    float chanceForMoreEffects(World world, double x, double y, double z, Random random);

}
