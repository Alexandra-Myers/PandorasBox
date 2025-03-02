/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectEntitiesCrush;
import ivorius.pandorasbox.random.*;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECCrushEntities(IValue time, DValue range, ZValue chanceForExtraCycles, IValue extraCycles) implements PBEffectCreator {
    public static final MapCodec<PBECCrushEntities> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("time").forGetter(PBECCrushEntities::time),
                            DValue.CODEC.fieldOf("range").forGetter(PBECCrushEntities::range),
                            ZValue.CODEC.optionalFieldOf("chance_for_extra_cycles", new ZChance(0.5)).forGetter(PBECCrushEntities::chanceForExtraCycles),
                            IValue.CODEC.optionalFieldOf("extra_cycles", new ILinear(1, 5)).forGetter(PBECCrushEntities::extraCycles))
                    .apply(instance, PBECCrushEntities::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int cycles = 1;
        if (chanceForExtraCycles.getValue(random)) {
            cycles += extraCycles.getValue(random);
        }

        int time = this.time.getValue(random);
        double range = this.range.getValue(random);
        double strength = (0.15 + random.nextDouble() * 0.15) * (1 + (cycles - 1) * 0.3);

        return new PBEffectEntitiesCrush(time, range, cycles, strength);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random)
    {
        return 0.15f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
