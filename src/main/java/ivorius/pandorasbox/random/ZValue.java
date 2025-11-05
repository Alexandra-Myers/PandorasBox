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
public interface ZValue
{
    ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends ZValue>> VALUE_MAPPER = new ExtraCodecs.LateBoundIdMapper<>();
    Codec<ZValue> CODEC = VALUE_MAPPER.codec(Identifier.CODEC)
            .dispatch(ZValue::codec, mapCodec -> mapCodec);
    static void bootstrap() {
        VALUE_MAPPER.put(Identifier.withDefaultNamespace("chance"), ZChance.CODEC);
        VALUE_MAPPER.put(Identifier.withDefaultNamespace("constant"), ZConstant.CODEC);
    }
    boolean getValue(RandomSource random);
    @NotNull MapCodec<? extends ZValue> codec();
}
