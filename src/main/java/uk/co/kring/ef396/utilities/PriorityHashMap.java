package uk.co.kring.ef396.utilities;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class PriorityHashMap<K, V> extends HashMap<K, V> {

    private HashMap<K, List<V>> violations = new HashMap<>();
    private boolean closed = false;

    @Override
    public V put(K key, V value) {
        if(!closed) {
            if (containsKey(value)) {
                LinkedList<V> vi = (LinkedList<V>) violations.getOrDefault(key, new LinkedList<V>());
                vi.add(value);
                violations.put(key, vi);
                return get(key);
            } else {
                return super.put(key, value);
            }
        } else {
            BaseCodeException.throwAssist(new StubbornException());//as it is difficult sometimes
            return null;
        }
    }

    public void close() {
        closed = true;
    }

    public V overwrite(K key, V value) {
        return super.put(key, value);
    }

    public List<V> getViolations(K key) {
        List<V> vi = violations.get(key);
        if(closed && vi instanceof LinkedList<V>) {
            vi = Collections.unmodifiableList(vi);
            violations.put(key, vi);
        }
        return vi;//return cached if closed
    }
}
