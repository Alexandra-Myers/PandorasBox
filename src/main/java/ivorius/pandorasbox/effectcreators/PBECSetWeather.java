/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectSetWeather;
import ivorius.pandorasbox.random.IValue;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECSetWeather(IValue weather, IValue rainTime, IValue delay) implements PBEffectCreator {
    public static final MapCodec<PBECSetWeather> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("weather").forGetter(PBECSetWeather::weather),
                            IValue.CODEC.fieldOf("rain_time").forGetter(PBECSetWeather::rainTime),
                            IValue.CODEC.fieldOf("delay").forGetter(PBECSetWeather::delay))
                    .apply(instance, PBECSetWeather::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int time = this.delay.getValue(random);
        int weather = this.weather.getValue(random);
        int rainTime = this.rainTime.getValue(random);

        return new PBEffectSetWeather(time, weather > 0, weather > 1, rainTime);
    }

    @Override
    public float chanceForMoreEffects(Level world, double x, double y, double z, RandomSource random) {
        return 0.8f;
    }

    @Override
    public @NotNull MapCodec<? extends PBEffectCreator> codec() {
        return CODEC;
    }
}
