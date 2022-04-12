package uk.co.kring.ef396.data;

import java.util.LinkedList;
import java.util.function.Predicate;

public class MTFLIst<T> extends LinkedList<T> {

    public T mtf(Predicate<T> find) {
        for (T element: this) {
            if(find.test(element)) {
                this.removeFirstOccurrence(element);
                this.addFirst(element);
                return element;
            }
        }
        return null;
    }
}
