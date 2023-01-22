package ivorius.pandorasbox.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ArrayListExtensions<T> extends ArrayList<T> implements IterableExtensions<T> {
    @SafeVarargs
    public final boolean addAll(T... entries) {
        return addAll(Arrays.asList(entries));
    }
    @SafeVarargs
    public final boolean addAll(Collection<T>... entries) {
        boolean bl = true;
        for(Collection<T> entry : entries) {
            bl &= addAll(entry);
        }
        return bl;
    }
    @SafeVarargs
    public final boolean addAll(int index, T... entries) {
        return addAll(index, Arrays.asList(entries));
    }
    @SafeVarargs
    public final boolean addAll(int index, Collection<T>... entries) {
        boolean bl = true;
        for(Collection<T> entry : entries) {
            bl &= addAll(index, entry);
        }
        return bl;
    }
}
