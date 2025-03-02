/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.effectcreators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import ivorius.pandorasbox.effects.PBEffect;
import ivorius.pandorasbox.effects.PBEffectSetTime;
import ivorius.pandorasbox.random.IValue;
import ivorius.pandorasbox.random.ZValue;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 30.03.14.
 */
public record PBECSetTime(IValue time, IValue worldTime, ZValue add) implements PBEffectCreator {
    public static final MapCodec<PBECSetTime> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(IValue.CODEC.fieldOf("time").forGetter(PBECSetTime::time),
                            IValue.CODEC.fieldOf("world_time").forGetter(PBECSetTime::worldTime),
                            ZValue.CODEC.fieldOf("add").forGetter(PBECSetTime::add))
                    .apply(instance, PBECSetTime::new));

    @Override
    public PBEffect constructEffect(Level world, double x, double y, double z, RandomSource random) {
        int time = this.time.getValue(random);
        int worldTime = this.worldTime.getValue(random);
        boolean add = this.add.getValue(random);

        if (!add) {
            int currentTime = (int) (world.getGameTime() % 24000);
            return new PBEffectSetTime(time, worldTime - currentTime);
        }
        else return new PBEffectSetTime(time, worldTime);
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
