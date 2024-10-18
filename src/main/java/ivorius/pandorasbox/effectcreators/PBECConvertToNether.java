/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.PandorasBoxHelper;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectGenConvertToNether;
import ivorius.pandorasbox.random.DValue;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECConvertToNether implements PBEffectCreator {
    public DValue range;
    public DValue chanceToDiscardNetherrack;
    public String biome;

    public PBECConvertToNether(DValue range, DValue chanceToDiscardNetherrack, String biome) {
        this.range = range;
        this.chanceToDiscardNetherrack = chanceToDiscardNetherrack;
        this.biome = biome;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        double range = this.range.getValue(random);
        int time = Mth.floor((random.nextDouble() * 7.0 + 3.0) * range);
        double discardChance = chanceToDiscardNetherrack.getValue(random);

        return new PBEffectGenConvertToNether(time, range, discardChance, PandorasBoxHelper.getRandomUnifiedSeed(random), biome);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.1f;
    }
}
