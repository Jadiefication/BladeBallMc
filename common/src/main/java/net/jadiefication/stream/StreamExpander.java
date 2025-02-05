package net.jadiefication.stream;

import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * An expansion to the base Stream<T> class with some useful functions.
 *
 * @Author: Jade
 *
 * @param <T>
 */
public interface StreamExpander<T> extends Stream<T> {

    default void forEachIndexed(BiConsumer<T, Integer> consumer) {
        Iterator<T> iterator = iterator();
        int index = 0;
        while (iterator.hasNext()) {
            consumer.accept(iterator.next(), index++);
        }
    }
}
