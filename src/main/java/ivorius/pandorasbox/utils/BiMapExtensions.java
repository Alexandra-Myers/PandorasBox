package ivorius.pandorasbox.utils;

import java.util.Map;

public interface BiMapExtensions<K, V> extends MapExtensions<K, V> {
    BiMapExtensions<V, K> inverse();
}
