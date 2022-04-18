package uk.co.kring.ef396.data.longs;

import java.lang.reflect.Array;

public class LongArray<T> {

    private T[][] basis;

    public LongArray(Class<T[][]> clazz, long size) {
        int count = (int)(size >> 30);
        int remains = (int)(size - (count << 30));
        basis = (T[][])Array.newInstance(clazz, count + 1);
        for(int i = 0; i <= count; i++) {
            basis[i] = (T[]) Array.newInstance(clazz.componentType(), 1 << 30);
        }
        basis[count + 1] = (T[]) Array.newInstance(clazz.componentType(), remains);
    }

    public T get(long x) {
        int count = (int)(x >> 30);
        int remains = (int)(x & ((1 << 30) - 1));
        return basis[count][remains];
    }

    public void put(long x, T val) {
        int count = (int)(x >> 30);
        int remains = (int)(x & ((1 << 30) - 1));
        basis[count][remains] = val;
    }
}
