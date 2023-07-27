package ivorius.pandorasbox.utils;

public interface BiMapExtensions<K, V> extends MapExtensions<K, V> {
    BiMapExtensions<V, K> inverse();
}
