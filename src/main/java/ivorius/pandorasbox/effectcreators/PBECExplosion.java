/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectExplode;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ZValue;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECExplosion(IValue time, DValue explosionRadius, ZValue burning, PBEffectExplode.ExplosionInteraction explosionInteraction) implements PBEffectCreator {
    public static final MapCodec<PBECExplosion> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("time").forGetter(PBECExplosion::time),
                            DValue.CODEC.fieldOf("radius").forGetter(PBECExplosion::explosionRadius),
                            ZValue.CODEC.fieldOf("burning").forGetter(PBECExplosion::burning),
                            PBEffectExplode.ExplosionInteraction.CODEC.fieldOf("explosion_interaction").forGetter(PBECExplosion::explosionInteraction))
                    .apply(instance, PBECExplosion::new));

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

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
