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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Created by lukas on 03.04.14.
 */
public class PBEffectSetTime extends PBEffectNormal {
    public static final MapCodec<PBEffectSetTime> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(base(),
                            Codec.INT.fieldOf("total_added").forGetter(PBEffectSetTime::getTotalPlus))
                    .apply(instance, PBEffectSetTime::new));
    public final int totalPlus;

    public int getTotalPlus() {
        return totalPlus;
    }

    public PBEffectSetTime(int maxTicksAlive, int totalPlus) {
        super(maxTicksAlive);
        this.totalPlus = totalPlus;
    }

    @Override
    public void doEffect(Level level, PandorasBoxEntity entity, Vec3 effectCenter, RandomSource random, float prevRatio, float newRatio) {
        if (level.isClientSide()) return;
        int newPlus = Mth.floor(totalPlus * newRatio);
        int prevPlus = Mth.floor(totalPlus * prevRatio);
        int plus = newPlus - prevPlus;

        for (ServerLevel serverlevel : Objects.requireNonNull(level.getServer()).getAllLevels()) {
            serverlevel.setDayTime(level.getDayTime() + plus);
        }
    }

    @Override
    public @NotNull MapCodec<? extends PBEffect> codec() {
        return CODEC;
    }
}
