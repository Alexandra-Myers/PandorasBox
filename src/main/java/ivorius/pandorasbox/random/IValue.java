/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 04.04.14.
 */
public interface IValue
{
    ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends IValue>> VALUE_MAPPER = new ExtraCodecs.LateBoundIdMapper<>();
    Codec<IValue> CODEC = VALUE_MAPPER.codec(Identifier.CODEC)
            .dispatch(IValue::codec, mapCodec -> mapCodec);
    static void bootstrap() {
        VALUE_MAPPER.put(Identifier.withDefaultNamespace("weighted"), IWeighted.CODEC);
        VALUE_MAPPER.put(Identifier.withDefaultNamespace("linear"), ILinear.CODEC);
        VALUE_MAPPER.put(Identifier.withDefaultNamespace("flags"), IFlags.CODEC);
        VALUE_MAPPER.put(Identifier.withDefaultNamespace("constant"), IConstant.CODEC);
        VALUE_MAPPER.put(Identifier.withDefaultNamespace("exponential"), IExp.CODEC);
    }
    int getValue(RandomSource random);
    @NotNull MapCodec<? extends IValue> codec();
}
