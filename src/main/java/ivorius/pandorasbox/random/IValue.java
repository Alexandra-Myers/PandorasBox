/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import ivorius.pandorasbox.utils.LateBoundIdMapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 04.04.14.
 */
public interface IValue
{
    LateBoundIdMapper<ResourceLocation, MapCodec<? extends IValue>> VALUE_MAPPER = new LateBoundIdMapper<>();
    Codec<IValue> CODEC = VALUE_MAPPER.codec(ResourceLocation.CODEC)
            .dispatch(IValue::codec, MapCodec::codec);
    static void bootstrap() {
        VALUE_MAPPER.put(new ResourceLocation("weighted"), IWeighted.CODEC);
        VALUE_MAPPER.put(new ResourceLocation("linear"), ILinear.CODEC);
        VALUE_MAPPER.put(new ResourceLocation("flags"), IFlags.CODEC);
        VALUE_MAPPER.put(new ResourceLocation("constant"), IConstant.CODEC);
        VALUE_MAPPER.put(new ResourceLocation("exponential"), IExp.CODEC);
    }
    int getValue(RandomSource random);
    @NotNull MapCodec<? extends IValue> codec();
}
