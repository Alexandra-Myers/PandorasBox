/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Created by lukas on 04.04.14.
 */
public record ValueSpawn(DValue spawnRange, DValue spawnShift) {
    public static final Codec<ValueSpawn> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(DValue.CODEC.fieldOf("spawn_range").forGetter(ValueSpawn::spawnRange),
                        DValue.CODEC.fieldOf("spawn_shift").forGetter(ValueSpawn::spawnShift))
                    .apply(instance, ValueSpawn::new));
}
