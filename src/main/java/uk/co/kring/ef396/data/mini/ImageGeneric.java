package uk.co.kring.ef396.data.mini;

public enum ImageGeneric {

    PLAYER(Group.PLAYER, 0, 16, 1, 1);

    private final Group kind;
    private final int frame, size, p, q;

    ImageGeneric(Group kind, int frame, int size, int p, int q) {
        this.kind = kind;
        this.frame = frame;
        this.size = size;
        this.p = p;
        this.q = q;
    }

    public int getFrame() {
        return frame;
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
}
