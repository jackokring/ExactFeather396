package uk.co.kring.ef396.utilities;

import java.util.HashMap;

public class PriorityHashMap<K, V> extends HashMap<K, V> {

    @Override
    public V put(K key, V value) {
        return putIfAbsent(key, value);
    }

    public V overwrite(K key, V value) {
        return super.put(key, value);
    }
}
