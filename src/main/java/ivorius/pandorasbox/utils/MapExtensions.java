package ivorius.pandorasbox.utils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("unused")
public interface MapExtensions<K, V> {
    /**
     * @return A new ArrayListExtensions containing all of this Map's keys.
     */
    default ArrayListExtensions<K> keyArray() {
        ArrayListExtensions<K> arrayList = new ArrayListExtensions<>();
        arrayList.addAll(map().keySet());
        return arrayList;
    }

    /**
     * @param list An ArrayListExtensions you would like to add this Map's keys onto.
     * @return The same ArrayListExtensions passed in as a parameter, now containing all keys from this map.
     */
    default ArrayListExtensions<K> keyArray(ArrayListExtensions<K> list) {
        list.addAll(map().keySet());
        return list;
    }
    /**
     * @return A new ArrayListExtensions containing all of this Map's Entries.
     */
    default ArrayListExtensions<Map.Entry<K, V>> entryArray() {
        ArrayListExtensions<Map.Entry<K, V>> arrayList = new ArrayListExtensions<>();
        arrayList.addAll(map().entrySet());
        return arrayList;
    }
    /**
     * @param list An ArrayListExtensions you would like to add this Map's Entries onto.
     * @return The same ArrayListExtensions passed in as a parameter, now containing all Entries from this map.
     */
    default ArrayListExtensions<Map.Entry<K, V>> entryArray(ArrayListExtensions<Map.Entry<K, V>> list) {
        list.addAll(map().entrySet());
        return list;
    }
    /**
     * @return A new ArrayListExtensions containing all of this Map's values.
     */
    default ArrayListExtensions<V> valueArray() {
        ArrayListExtensions<V> arrayList = new ArrayListExtensions<>();
        arrayList.addAll(map().values());
        return arrayList;
    }
    /**
     * @param list An ArrayListExtensions you would like to add this Map's values onto.
     * @return The same ArrayListExtensions passed in as a parameter, now containing all values from this map.
     */
    default ArrayListExtensions<V> valueArray(ArrayListExtensions<V> list) {
        list.addAll(map().values());
        return list;
    }
    /**
     * @return Returns the Map stored by the WrappedMap, used for modifications to the map itself across several methods.
     */
    Map<K, V> map();

    /**
     * Sets modifications made for the map onto the map stored in the WrappedMap
     * @param map The modified map to set the WrappedMap's map to.
     */
    void setMap(Map<K, V> map);

    default int size() {
        return map().size();
    }

    default boolean isEmpty() {
        return map().isEmpty();
    }

    default boolean containsKey(K key) {
        return map().containsKey(key);
    }

    default boolean containsValue(V value) {
        return map().containsValue(value);
    }


    default V get(K key) {
        return map().get(key);
    }

    default V put(K key, V value) {
        Map<K, V> map = map();
        V newValue = map.put(key, value);
        setMap(map);
        return newValue;
    }

    default V remove(K key) {
        Map<K, V> map = map();
        V removedValue = map.remove(key);
        setMap(map);
        return removedValue;
    }

    default void putAll(Map<? extends K, ? extends V> m) {
        Map<K, V> map = map();
        map.putAll(m);
        setMap(map);
    }

    default void clear() {
        Map<K, V> map = map();
        map.clear();
        setMap(map);
    }

    default Set<K> keySet() {
        return map().keySet();
    }

    default Collection<V> values() {
        return map().values();
    }

    default Set<Map.Entry<K, V>> entrySet() {
        return map().entrySet();
    }

    default boolean mapEquals(Object o) {
        return map().equals(o);
    }

    default int mapHashCode() {
        return map().hashCode();
    }

    default V getOrDefault(K key, V defaultValue) {
        return map().getOrDefault(key, defaultValue);
    }

    default void forEach(BiConsumer<? super K, ? super V> action) {
        Map<K, V> map = map();
        map.forEach(action);
        setMap(map);
    }

    default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        Map<K, V> map = map();
        map.replaceAll(function);
        setMap(map);
    }

    default V putIfAbsent(K key, V value) {
        Map<K, V> map = map();
        V v = map.putIfAbsent(key, value);
        setMap(map);
        return v;
    }

    default boolean remove(K key, V value) {
        Map<K, V> map = map();
        boolean v = map.remove(key, value);
        setMap(map);
        return v;
    }

    default boolean replace(K key, V oldValue, V newValue) {
        Map<K, V> map = map();
        boolean v = map.replace(key, oldValue, newValue);
        setMap(map);
        return v;
    }

    default V replace(K key, V value) {
        Map<K, V> map = map();
        V v = map.replace(key, value);
        setMap(map);
        return v;
    }

    default V computeIfAbsent(K key,
                              Function<? super K, ? extends V> mappingFunction) {
        Map<K, V> map = map();
        V v = map.computeIfAbsent(key, mappingFunction);
        setMap(map);
        return v;
    }

    default V computeIfPresent(K key,
                               BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Map<K, V> map = map();
        V v = map.computeIfPresent(key, remappingFunction);
        setMap(map);
        return v;
    }

    default V compute(K key,
                      BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        Map<K, V> map = map();
        V v = map.compute(key, remappingFunction);
        setMap(map);
        return v;
    }

    default V merge(K key, V value,
                    BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Map<K, V> map = map();
        V v = map.merge(key, value, remappingFunction);
        setMap(map);
        return v;
    }
}
