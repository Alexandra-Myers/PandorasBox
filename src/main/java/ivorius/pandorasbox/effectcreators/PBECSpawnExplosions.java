/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectPositionBased;
import ivorius.pandorasbox.effects.position.RandomExplosionsPositionEffect;
import ivorius.pandorasbox.random.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECSpawnExplosions(IValue time, IValue number, DValue range, DValue explosionStrength, ZValue isFlaming, ZValue isSmoking) implements PBEffectCreator {
    public static final MapCodec<PBECSpawnExplosions> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("time").forGetter(PBECSpawnExplosions::time),
                            IValue.CODEC.fieldOf("number").forGetter(PBECSpawnExplosions::number),
                            DValue.CODEC.fieldOf("range").forGetter(PBECSpawnExplosions::range),
                            DValue.CODEC.fieldOf("explosion_strength").forGetter(PBECSpawnExplosions::explosionStrength),
                            ZValue.CODEC.optionalFieldOf("is_flaming", new ZChance(0.3)).forGetter(PBECSpawnExplosions::isFlaming),
                            ZValue.CODEC.optionalFieldOf("is_smoking", new ZConstant(true)).forGetter(PBECSpawnExplosions::isSmoking))
                    .apply(instance, PBECSpawnExplosions::new));
    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int time = this.time.getValue(random);
        int number = this.number.getValue(random);
        double range = this.range.getValue(random);
        double[] strength = ValueHelper.getValueRange(explosionStrength, random);
        boolean isFlaming = this.isFlaming.getValue(random);
        boolean isSmoking = this.isSmoking.getValue(random);

        return new PBEffectPositionBased(time, number, range, new RandomExplosionsPositionEffect((float) strength[0], (float) strength[1], isFlaming, isSmoking));
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.7f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
