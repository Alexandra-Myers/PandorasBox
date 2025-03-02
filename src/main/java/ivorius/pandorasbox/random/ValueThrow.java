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
public record ValueThrow(DValue throwStrengthSide, DValue throwStrengthY) {
    public static final Codec<ValueThrow> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(DValue.CODEC.fieldOf("throw_strength_horizontal").forGetter(ValueThrow::throwStrengthSide),
                            DValue.CODEC.fieldOf("throw_strength_vertical").forGetter(ValueThrow::throwStrengthY))
                    .apply(instance, ValueThrow::new));
}
