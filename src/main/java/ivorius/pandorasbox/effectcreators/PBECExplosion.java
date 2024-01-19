/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectExplode;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ZValue;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

/**
 * Created by lukas on 30.03.14.
 */
public class PBECExplosion implements PBEffectCreator {
    public IValue time;
    public DValue explosionRadius;
    public ZValue burning;
    public Level.ExplosionInteraction explosionInteraction;

    public PBECExplosion(IValue time, DValue explosionRadius, ZValue burning, Level.ExplosionInteraction interactionType) {
        this.time = time;
        this.explosionRadius = explosionRadius;
        this.burning = burning;
    }

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int time = this.time.getValue(random);
        double explosionRadius = this.explosionRadius.getValue(random);
        boolean burning = this.burning.getValue(random);

        return new PBEffectExplode(time, (float) explosionRadius, burning, explosionInteraction);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return 0.5f;
    }
}
