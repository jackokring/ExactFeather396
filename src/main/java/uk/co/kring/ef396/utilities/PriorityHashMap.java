package uk.co.kring.ef396.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PriorityHashMap<K, V> extends HashMap<K, V> {

    private HashMap<K, ArrayList<V>> violations = new HashMap<>();

    @Override
    public V put(K key, V value) {
        if(containsKey(value)) {
            violations.get(key).add(value);
            return get(key);
        } else {
            return super.put(key, value);
        }
    }

    public V overwrite(K key, V value) {
        return super.put(key, value);
    }

    public List<V> getViolations(K key) {
        return Collections.unmodifiableList(violations.get(key));
    }
}
