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
public interface ZValue
{
    LateBoundIdMapper<ResourceLocation, MapCodec<? extends ZValue>> VALUE_MAPPER = new LateBoundIdMapper<>();
    Codec<ZValue> CODEC = VALUE_MAPPER.codec(ResourceLocation.CODEC)
            .dispatch(ZValue::codec, MapCodec::codec);
    static void bootstrap() {
        VALUE_MAPPER.put(new ResourceLocation("chance"), ZChance.CODEC);
        VALUE_MAPPER.put(new ResourceLocation("constant"), ZConstant.CODEC);
    }
    boolean getValue(RandomSource random);
    @NotNull MapCodec<? extends ZValue> codec();
}
