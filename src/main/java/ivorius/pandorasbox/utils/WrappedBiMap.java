package ivorius.pandorasbox.utils;

import com.google.common.collect.BiMap;

import java.util.Map;

public class WrappedBiMap<K, V> extends WrappedMap<K, V> implements BiMapExtensions<K, V> {
    BiMap<K, V> map;
    public WrappedBiMap(BiMap<K, V> base) {
        super(base);
        map = base;
    }

    @Override
    public Map<K, V> map() {
        return map;
    }

    @Override
    public void setMap(Map<K, V> map) {
        this.map = (BiMap<K, V>) map;
    }

    @Override
    public BiMapExtensions<V, K> inverse() {
        return new WrappedBiMap<>(map.inverse());
    }
}
