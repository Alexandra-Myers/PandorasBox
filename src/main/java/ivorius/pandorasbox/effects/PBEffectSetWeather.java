/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.entitites.PandorasBoxEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectSetWeather extends PBEffectNormal {
    public static final MapCodec<PBEffectSetWeather> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(base(),
                            Codec.BOOL.fieldOf("rain").forGetter(PBEffectSetWeather::isRain),
                            Codec.BOOL.fieldOf("thunder").forGetter(PBEffectSetWeather::isThunder),
                            Codec.INT.fieldOf("rain_time").forGetter(PBEffectSetWeather::getRainTime))
                    .apply(instance, PBEffectSetWeather::new));
    public final boolean rain;
    public final boolean thunder;
    public final int rainTime;

    public PBEffectSetWeather(int maxTicksAlive, boolean rain, boolean thunder, int rainTime) {
        super(maxTicksAlive);
        this.rain = rain;
        this.thunder = thunder;
        this.rainTime = rainTime;
    }

    public boolean isRain() {
        return rain;
    }

    public boolean isThunder() {
        return thunder;
    }

    public int getRainTime() {
        return rainTime;
    }

    @Override
    public void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {
    }

    @Override
    public void finalizeEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random) {
        if (level instanceof ServerLevel serverLevel) {
            if (serverLevel.isRaining() && !rain) {
                serverLevel.setWeatherParameters(rainTime, 0, false, false);
                return;
            }
            serverLevel.setWeatherParameters(0, rainTime, rain, thunder);
        }
    }

    @Override
    public @NotNull MapCodec<? extends PBEffect> codec() {
        return CODEC;
    }
}
