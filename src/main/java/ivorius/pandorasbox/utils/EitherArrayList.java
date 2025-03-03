package ivorius.pandorasbox.utils;

import com.mojang.datafixers.util.Either;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Special form of {@link ArrayList} used to contain {@link Either} instances, storing multiple data types into one list.
 * @see ArrayList
 * @see Either
 * @param <L> The left-side data type.
 * @param <R> The right-side data type.
 */
public class EitherArrayList<L, R> extends ArrayList<Either<L, R>> {
    public EitherArrayList(int initialCapacity) {
        super(initialCapacity);
    }

    public EitherArrayList() {
        super();
    }

    public EitherArrayList(Collection<? extends Either<L, R>> c) {
        super(c);
    }

    public Set<L> leftSide() {
        return stream().filter(lrEither -> lrEither.left().isPresent()).map(lrEither -> lrEither.left().get()).collect(Collectors.toSet());
    }

    public Set<R> rightSide() {
        return stream().filter(lrEither -> lrEither.right().isPresent()).map(lrEither -> lrEither.right().get()).collect(Collectors.toSet());
    }

    public L getL(int index) {
        return get(index).orThrow();
    }

    public R getR(int index) {
        return get(index).swap().orThrow();
    }

    public boolean addLeft(L obj) {
        return add(Either.left(obj));
    }

    public void addLeft(int index, L obj) {
        add(index, Either.left(obj));
    }

    public boolean addRight(R obj) {
        return add(Either.right(obj));
    }

    public void addRight(int index, R obj) {
        add(index, Either.right(obj));
    }

    /**
     * Maps this list into an unmodifiable collection of a different type.
     * @param leftMapper - The mapper from elements of type L to T.
     * @param rightMapper - The mapper from elements of type R to T.
     * @return A collection made up of the contents of this list, mapped onto type T using the mapper functions.
     * @param <T> The new type produced from the mapping.
     */
    public <T> List<T> map(Function<L, T> leftMapper, Function<R, T> rightMapper) {
        return stream().map(either -> either.map(leftMapper, rightMapper)).toList();
    }
}
