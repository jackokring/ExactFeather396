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

    public V get(Object key) {
        if(containsKey(key) && super.get(key) == null) {
            return super.get(null);//template blank
        } else {
            return super.get(key);//so null empty
        }
    }

    public V put(K key) {
        // lock entry to null
        V g = get(key);
        put(key, null);
        return g;
    }

    public V progress(K key) {
        if(!closed) {
            List<V> vi;
            V val = null;
            if ((vi = violations.get(key)) != null) {
                if ((val = vi.get(0)) != null) {
                    overwrite(key, val);
                    vi.remove(0);
                }
            }
            return val;
        } else {
            BaseCodeException.throwAssist(msg, new StubbornException());
            return null;
        }
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
        if(closed && vi != null && vi instanceof LinkedList<V>) {
            vi = Collections.unmodifiableList(vi);
            violations.put(key, vi);
        }
        return vi;//return cached if closed
    }
}
