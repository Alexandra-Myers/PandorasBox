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
public interface DValue {
    LateBoundIdMapper<ResourceLocation, MapCodec<? extends DValue>> VALUE_MAPPER = new LateBoundIdMapper<>();
    Codec<DValue> CODEC = VALUE_MAPPER.codec(ResourceLocation.CODEC)
            .dispatch(DValue::codec, mapCodec -> mapCodec);
    static void bootstrap() {
        VALUE_MAPPER.put(ResourceLocation.withDefaultNamespace("weighted"), DWeighted.CODEC);
        VALUE_MAPPER.put(ResourceLocation.withDefaultNamespace("linear"), DLinear.CODEC);
        VALUE_MAPPER.put(ResourceLocation.withDefaultNamespace("gaussian"), DGaussian.CODEC);
        VALUE_MAPPER.put(ResourceLocation.withDefaultNamespace("constant"), DConstant.CODEC);
        VALUE_MAPPER.put(ResourceLocation.withDefaultNamespace("exponential"), DExp.CODEC);
    }
    double getValue(RandomSource random);
    @NotNull MapCodec<? extends DValue> codec();
}
