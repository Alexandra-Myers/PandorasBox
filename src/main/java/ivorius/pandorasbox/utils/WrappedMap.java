package ivorius.pandorasbox.utils;

import java.util.Map;

public class WrappedMap<K, V> implements MapExtensions<K, V> {
    Map<K, V> map;
    WrappedMap(Map<K, V> base) {
        map = base;
    }
    @Override
    public Map<K, V> map() {
        return map;
    }

    @Override
    public void setMap(Map<K, V> map) {
        this.map = map;
    }
}
