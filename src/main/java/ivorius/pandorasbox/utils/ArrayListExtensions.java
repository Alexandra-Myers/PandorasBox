package ivorius.pandorasbox.utils;

import java.util.ArrayList;
import java.util.Arrays;

public class ArrayListExtensions<T> extends ArrayList<T> implements IterableExtensions<T> {
    @SafeVarargs
    public final boolean addAll(T... entries) {
        return addAll(Arrays.asList(entries));
    }
    @SafeVarargs
    public final boolean addAll(int index, T... entries) {
        return addAll(index, Arrays.asList(entries));
    }
}
