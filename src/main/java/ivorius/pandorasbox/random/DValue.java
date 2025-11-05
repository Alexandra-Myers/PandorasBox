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
public interface DValue {
    ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends DValue>> VALUE_MAPPER = new ExtraCodecs.LateBoundIdMapper<>();
    Codec<DValue> CODEC = VALUE_MAPPER.codec(Identifier.CODEC)
            .dispatch(DValue::codec, mapCodec -> mapCodec);
    static void bootstrap() {
        VALUE_MAPPER.put(Identifier.withDefaultNamespace("weighted"), DWeighted.CODEC);
        VALUE_MAPPER.put(Identifier.withDefaultNamespace("linear"), DLinear.CODEC);
        VALUE_MAPPER.put(Identifier.withDefaultNamespace("gaussian"), DGaussian.CODEC);
        VALUE_MAPPER.put(Identifier.withDefaultNamespace("constant"), DConstant.CODEC);
        VALUE_MAPPER.put(Identifier.withDefaultNamespace("exponential"), DExp.CODEC);
    }
    double getValue(RandomSource random);
    @NotNull MapCodec<? extends DValue> codec();
}
