/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.utils.LateBoundIdMapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 04.04.14.
 */
public interface IValue
{
    LateBoundIdMapper<ResourceLocation, MapCodec<? extends IValue>> VALUE_MAPPER = new LateBoundIdMapper<>();
    Codec<IValue> CODEC = VALUE_MAPPER.codec(ResourceLocation.CODEC)
            .dispatch(IValue::codec, mapCodec -> mapCodec);
    static void bootstrap() {
        VALUE_MAPPER.put(ResourceLocation.withDefaultNamespace("weighted"), IWeighted.CODEC);
        VALUE_MAPPER.put(ResourceLocation.withDefaultNamespace("linear"), ILinear.CODEC);
        VALUE_MAPPER.put(ResourceLocation.withDefaultNamespace("flags"), IFlags.CODEC);
        VALUE_MAPPER.put(ResourceLocation.withDefaultNamespace("constant"), IConstant.CODEC);
        VALUE_MAPPER.put(ResourceLocation.withDefaultNamespace("exponential"), IExp.CODEC);
    }
    int getValue(RandomSource random);
    @NotNull MapCodec<? extends IValue> codec();
}
