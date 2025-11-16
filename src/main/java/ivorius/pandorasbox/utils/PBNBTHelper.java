package ivorius.pandorasbox.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by lukas on 03.02.15.
 */
public class PBNBTHelper {
    public static Codec<Component> COMPONENT_CODEC = Codec.of(new Encoder<>() {
        @Override
        public <T> DataResult<T> encode(Component input, DynamicOps<T> ops, T prefix) {
            JsonElement jsonElement = Component.Serializer.toJsonTree(input);
            if (jsonElement instanceof JsonObject jsonObject) {
                Map<T, T> toAdd = Map.ofEntries(jsonObject.entrySet().stream().map(entry -> {
                    T newKey = JsonOps.INSTANCE.convertTo(ops, JsonOps.INSTANCE.createString(entry.getKey()));
                    T newValue = JsonOps.INSTANCE.convertTo(ops, entry.getValue());
                    return new PBNBTHelper.AlikeEntry(newKey, newValue);
                }).toArray(PBNBTHelper.AlikeEntry[]::new));
                return ops.mergeToMap(prefix, toAdd);
            } else if (jsonElement instanceof JsonArray jsonArray) {
                List<T> toAdd = jsonArray.asList().stream().map(jsonElement1 -> JsonOps.INSTANCE.convertTo(ops, jsonElement1)).toList();
                return ops.mergeToList(prefix, toAdd);
            }
            return ops.mergeToPrimitive(prefix, JsonOps.INSTANCE.convertTo(ops, jsonElement));
        }
    }, new Decoder<>() {
        @Override
        public <T> DataResult<Pair<Component, T>> decode(DynamicOps<T> ops, T input) {
            JsonElement element = ops.convertTo(JsonOps.INSTANCE, input);
            MutableComponent component = Component.Serializer.fromJson(element);
            return DataResult.success(Pair.of(component, input));
        }
    });
    public static <E> Codec<E[]> arrayCodec(Codec<E> codec, Supplier<E[]> arrCreator) {
        return codec.listOf().xmap(list -> list.toArray(arrCreator.get()), elements -> Collections.unmodifiableList(Arrays.asList(elements)));
    }
    public static <T, AT> Codec<T> withAlternative(Codec<T> defaultCodec, Codec<AT> alternativeCodec, Function<AT, T> mapper) {
        return Codec.either(defaultCodec, alternativeCodec).xmap(tatEither -> tatEither.map(Function.identity(), mapper), Either::left);
    }
    public static <T, AT extends T> Codec<T> withAlternative(Codec<T> defaultCodec, Codec<AT> alternativeCodec) {
        return Codec.either(defaultCodec, alternativeCodec).xmap(tatEither -> tatEither.map(Function.identity(), Function.identity()), Either::left);
    }
    public static <A> Codec<Optional<A>> optionalEmptyMap(Codec<A> codec) {
        return new Codec<>() {
            @Override
            public <T> DataResult<Pair<Optional<A>, T>> decode(DynamicOps<T> dynamicOps, T object) {
                return isEmptyMap(dynamicOps, object)
                        ? DataResult.success(Pair.of(Optional.empty(), object))
                        : codec.decode(dynamicOps, object).map(pair -> pair.mapFirst(Optional::of));
            }

            private static <T> boolean isEmptyMap(DynamicOps<T> dynamicOps, T object) {
                Optional<MapLike<T>> optional = dynamicOps.getMap(object).result();
                return optional.isPresent() && ((MapLike) optional.get()).entries().findAny().isEmpty();
            }

            public <T> DataResult<T> encode(Optional<A> optional, DynamicOps<T> dynamicOps, T object) {
                return optional.isEmpty() ? DataResult.success(dynamicOps.emptyMap()) : codec.encode((A) optional.get(), dynamicOps, object);
            }
        };
    }
    public record AlikeEntry<T>(T newKey, T newValue) implements Map.Entry<T, T> {
        @Override
        public T getKey() {
            return newKey;
        }

        @Override
        public T getValue() {
            return newValue;
        }

        @Override
        public T setValue(Object value) {
            return newValue;
        }
    }
}
