package org.soak.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.mose.collection.stream.builder.CollectionStreamBuilder;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;

public class ListMappingUtils {

    public static <E, T> CollectionStreamBuilder.SortedRules<E, T> fromStream(CollectionStreamBuilder.Rules<E, T> builder, Supplier<Stream<E>> collectionGetter, BiPredicate<E, T> compare, Comparator<E> sort) {
        ToIntFunction<T> indexOf = value -> {
            AtomicInteger count = new AtomicInteger(0);
            collectionGetter.get().sorted(sort).filter(spongeValue -> {
                boolean is = compare.test(spongeValue, value);
                if (is) {
                    return true;
                }
                count.addAndGet(1);
                return false;
            }).findAny();
            return count.get();
        };

        return builder.withFirstIndexOf(indexOf);
    }

    public static <E, T> List<T> direct(List<E> list, Function<E, T> to, Function<T, E> from, boolean modifiable){
        var builder = (CollectionStreamBuilder.SortedRules<E, T>) CollectionStreamBuilder
                .builder()
                .collection(list, from)
                .basicMap(to)
                .withClear(list::clear)
                .withRemoveAll(collection -> list.removeAll(collection.stream().map(t -> (T)t).map(from).toList()))
                .withFirstIndexOf(find -> list.indexOf(from.apply(find)))
                .withParallel(list::parallelStream)
                .withAddToIndex((index, collection) -> list.addAll(index, collection.stream().map(from).toList()))
                .withSet((index, value) -> to.apply(list.set(index, from.apply(value))))
                .withLastIndexOf(find -> list.lastIndexOf(from.apply(find)))
                .withEquals((check, compare) -> from.apply(check).equals(compare)); //this should return sortedRules -> will fix
        return builder.buildList(modifiable);
    }
}
