package ivorius.pandorasbox.weighted;

import com.google.common.collect.Collections2;
import net.minecraft.util.RandomSource;

import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class WeightedSelector
{
    public static <T extends Item> double totalWeight(Collection<T> items)
    {
        return items.stream().mapToDouble(Item::weight).reduce(0, Double::sum);
    }

    public static <T> double totalWeight(Collection<T> items, final ToDoubleFunction<T> weightFunction)
    {
        return items.stream().mapToDouble(weightFunction).reduce(0, Double::sum);
    }

    public static boolean canSelect(Collection<? extends Item> items)
    {
        return items.stream().anyMatch(item -> item.weight() > 0);
    }

    public static <T> boolean canSelect(Collection<T> items, ToDoubleFunction<T> weightFunction)
    {
        return items.stream().anyMatch(item -> weightFunction.applyAsDouble(item) > 0);
    }

    public static <T> T selectWeightless(RandomSource rand, Collection<T> items, int counted)
    {
        counted = rand.nextInt(counted);
        for (Iterator<T> iterator = items.iterator(); true; )
        {
            T item = iterator.next();
            if (counted-- == 0 || !iterator.hasNext())
                return item;
        }
    }

    public static <T extends Item> T selectItem(RandomSource rand, Collection<T> items)
    {
        return selectItem(rand, items, totalWeight(items));
    }

    public static <T extends Item> T selectItem(RandomSource rand, Collection<T> items, double totalWeight)
    {
        return selectItem(rand, items, totalWeight, false);
    }

    public static <T extends Item> T selectItem(RandomSource rand, Collection<T> items, boolean remove)
    {
        return selectItem(rand, items, totalWeight(items), remove);
    }

    public static <T extends Item> T selectItem(RandomSource rand, Collection<T> items, double totalWeight, boolean remove)
    {
        if (items.size() == 0)
            throw new IndexOutOfBoundsException();

        double random = rand.nextDouble() * totalWeight;
        int counted = 0;
        for (Iterator<T> iterator = items.iterator(); iterator.hasNext(); counted++)
        {
            T t = iterator.next();
            random -= t.weight();
            if (random <= 0.0)
            {
                if (remove)
                    iterator.remove();
                return t;
            }
        }

        return selectWeightless(rand, items, counted);
    }

    public static <T> T select(RandomSource rand, Collection<T> items, final ToDoubleFunction<T> weightFunction)
    {
        return select(rand, items, weightFunction, totalWeight(items, weightFunction));
    }

    public static <T> T select(RandomSource rand, Collection<T> items, final ToDoubleFunction<T> weightFunction, double totalWeight)
    {
        return select(rand, items, weightFunction, totalWeight, false);
    }

    public static <T> T select(RandomSource rand, Collection<T> items, final ToDoubleFunction<T> weightFunction, boolean remove)
    {
        return select(rand, items, weightFunction, totalWeight(items, weightFunction), remove);
    }

    public static <T> T select(RandomSource rand, Collection<T> items, final ToDoubleFunction<T> weightFunction, double totalWeight, boolean remove)
    {
        if (items.size() == 0)
            throw new IndexOutOfBoundsException();

        double random = rand.nextDouble() * totalWeight;
        int counted = 0;
        for (Iterator<T> iterator = items.iterator(); iterator.hasNext(); counted++)
        {
            T t = iterator.next();
            random -= weightFunction.applyAsDouble(t);
            if (random <= 0.0)
            {
                if (remove)
                    iterator.remove();
                return t;
            }
        }

        return selectWeightless(rand, items, counted);
    }

    public static <T> T select(RandomSource rand, Collection<SimpleItem<T>> items)
    {
        return selectItem(rand, items).item();
    }

    public static <T> T select(RandomSource rand, Collection<SimpleItem<T>> items, double totalWeight)
    {
        return selectItem(rand, items, totalWeight).item();
    }

    public static <T> T select(RandomSource rand, Collection<SimpleItem<T>> items, boolean remove)
    {
        return selectItem(rand, items, remove).item();
    }

    public static <T> T select(RandomSource rand, Collection<SimpleItem<T>> items, double totalWeight, boolean remove)
    {
        return selectItem(rand, items, totalWeight, remove).item();
    }

    public interface Item
    {
        double weight();
    }

    public record SimpleItem<T>(double weight, T item) implements Item, Comparable<Item> {

        public static <T> SimpleItem<T> of(double weight, T item) {
            return new SimpleItem<>(weight, item);
        }

        public static <T> List<SimpleItem<T>> apply(Stream<T> items, final ToDoubleFunction<T> weightFunction) {
            return items.map(input -> new SimpleItem<>(weightFunction.applyAsDouble(input), input)).collect(Collectors.toList());
        }

        public static <T> Collection<SimpleItem<T>> apply(Collection<T> items, final ToDoubleFunction<T> weightFunction) {
            return Collections2.transform(items, item -> new SimpleItem<>(weightFunction.applyAsDouble(item), item));
        }

        public static <T> List<SimpleItem<T>> apply(List<T> items, final ToDoubleFunction<T> weightFunction) {
            return apply(items.stream(), weightFunction);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SimpleItem<?> that = (SimpleItem<?>) o;

            if (Double.compare(that.weight, weight) != 0) return false;
            return Objects.equals(item, that.item);
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(weight);
            result = (int) (temp ^ (temp >>> 32));
            result = 31 * result + (item != null ? item.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "SimpleItem{" +
                    "weight=" + weight +
                    ", item=" + item +
                    '}';
        }

        @Override
        public int compareTo(Item o) {
            return Double.compare(weight, o.weight());
        }
    }

    public static class ItemComparator implements Comparator<Item>
    {
        @Override
        public int compare(Item o1, Item o2)
        {
            return Double.compare(o1.weight(), o2.weight());
        }
    }
}