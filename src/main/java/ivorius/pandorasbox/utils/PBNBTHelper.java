package ivorius.pandorasbox.utils;

import com.mojang.serialization.Codec;

import java.util.Arrays;
import java.util.Collections;
import java.util.function.Supplier;

/**
 * Created by lukas on 03.02.15.
 */
public class PBNBTHelper {
    public static <E> Codec<E[]> arrayCodec(Codec<E> codec, Supplier<E[]> arrCreator) {
        return codec.listOf().xmap(list -> list.toArray(arrCreator.get()), elements -> Collections.unmodifiableList(Arrays.asList(elements)));
    }
}
