package net.jadiefication.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public interface MapExtender<K, V> extends Map<K, V> {

    default boolean hasListValue(Object object) throws ValueTypeException {
        AtomicBoolean isTrue = new AtomicBoolean(false);
        if (values().stream().anyMatch(value -> value instanceof Collection<?>)) {
            values().forEach(value -> {
                Collection<?> valueAsList = ((Collection<?>) value);
                if (valueAsList.contains(object)) {
                    isTrue.set(true);
                }
            });
        } else {
            throw new ValueTypeException();
        }
        return isTrue.get();
    }

    default K getKey(Object value) {
        AtomicReference<K> atomicValue = new AtomicReference<K>();
        entrySet().forEach((entry -> {
            if (entry.getValue().equals(value)) {
                atomicValue.set(entry.getKey());
            }
        }));
        return atomicValue.get();
    }

    default List<V> getValues() {
        List<V> values = new ArrayList<>();
        forEach((key, value) -> values.add(value));

        return values;
    }

    default K getKeyByListValue(Object object) {
        if (hasListValue(object)) {
            AtomicReference<List<?>> correctValue = new AtomicReference<>();
            values().forEach(value -> {
                Collection<?> valueAsList = ((Collection<?>) value);
                if (valueAsList.contains(object)) {
                    correctValue.set(valueAsList.stream().toList());
                }
            });
            if (correctValue.get() != null) {
                return getKey(correctValue.get());
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    default List<K> getKeys() {
        List<K> values = new ArrayList<>();
        forEach((key, value) -> values.add(key));

        return values;
    }
}
