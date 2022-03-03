package uk.co.kring.ef396.utilities;

import uk.co.kring.ef396.utilities.exceptions.BaseCodeException;
import uk.co.kring.ef396.utilities.exceptions.StubbornException;

import java.util.*;

public class PriorityHashMap<K, V> extends HashMap<K, V> {

    private final HashMap<K, List<V>> violations = new HashMap<>();
    private boolean closed = false;
    private static final String msg = "Closed PriorityMap";

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
            BaseCodeException.throwAssist(msg, new StubbornException());//as it is difficult sometimes
            return null;
        }
    }

    public Optional<V> progress(K key) {
        List<V> vi;
        V val = null;
        if((vi = violations.get(key)) != null) {
            if((val = vi.get(0)) != null) {
                overwrite(key, val);
                vi.remove(0);
            }
        }
        return Optional.ofNullable(val);
    }

    public void close() {
        closed = true;
    }

    public V overwrite(K key, V value) {
        if(!closed) {
            return super.put(key, value);
        } else {
            BaseCodeException.throwAssist(msg, new StubbornException());
            return null;
        }
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
