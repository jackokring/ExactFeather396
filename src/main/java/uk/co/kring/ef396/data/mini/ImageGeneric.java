package uk.co.kring.ef396.data.mini;

import java.util.function.IntUnaryOperator;

public enum ImageGeneric {

    PLAYER(Group.PLAYER,16, 1, 1, (idx) -> 0);

    private final Group kind;
    private final int size, p, q;
    private final IntUnaryOperator boundMap;

    ImageGeneric(Group kind, int size, int p, int q, IntUnaryOperator boundMap) {
        this.kind = kind;
        this.size = size;
        this.p = p;
        this.q = q;
        this.boundMap = boundMap;
    }

    public int getSize() {
        return size;
    }

    public Group getKind() {
        return kind;
    }

    public int getScale() {//essentially gets collision height and width
        return size * p / q;
    }

    public int getBound(int idx) {
        return boundMap.applyAsInt(idx);
    }
}
