/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectRandomLightnings;
import ivorius.pandorasbox.random.DValue;
import ivorius.pandorasbox.random.IValue;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECSpawnLightning(IValue time, IValue number, DValue range) implements PBEffectCreator {
    public static final MapCodec<PBECSpawnLightning> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("time").forGetter(PBECSpawnLightning::time),
                            IValue.CODEC.fieldOf("number").forGetter(PBECSpawnLightning::number),
                            DValue.CODEC.fieldOf("range").forGetter(PBECSpawnLightning::range))
                    .apply(instance, PBECSpawnLightning::new));
    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int time = this.time.getValue(random);
        int number = this.number.getValue(random);
        double range = this.range.getValue(random);

        return new PBEffectRandomLightnings(time, number, range);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return 0.7f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
