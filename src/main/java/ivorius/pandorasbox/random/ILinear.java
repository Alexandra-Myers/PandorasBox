/*
 * Copyright (c) 2014, Lukas Tenbrink.
 * http://lukas.axxim.net
 */

package ivorius.pandorasbox.random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

/**
 * Created by lukas on 04.04.14.
 */
public record ILinear(int min, int max) implements IValue {
    public static final MapCodec<ILinear> CODEC = RecordCodecBuilder.<ILinear>mapCodec(instance ->
            instance.group(Codec.INT.fieldOf("min").forGetter(ILinear::min),
                            Codec.INT.fieldOf("max").forGetter(ILinear::max))
                    .apply(instance, ILinear::new)).validate(iLinear -> {
                        if (iLinear.min > iLinear.max) return DataResult.error(() -> "Constraints for linear random mismatched, min greater than max!");
                        else return DataResult.success(iLinear);
    });
    @Override
    public int getValue(RandomSource random) {
        return min + random.nextInt(max - min + 1);
    }

    @Override
    public @NotNull MapCodec<? extends IValue> codec() {
        return CODEC;
    }
}
