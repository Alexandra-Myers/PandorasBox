package ivorius.pandorasbox.utils;

public interface IterableExtensions<T> extends Iterable<T> {
    default Iterable<T> asIterable() {
        return this;
    }
}
