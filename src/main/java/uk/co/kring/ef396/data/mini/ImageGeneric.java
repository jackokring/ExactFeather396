package uk.co.kring.ef396.data.mini;

import uk.co.kring.ef396.data.components.ImageCanvas;

import java.awt.image.BufferedImage;

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

    public int getScale() {//essentially gets collision height and width
        return size * p / q;
    }

    public void draw(ImageCanvas ic, int x, int y, int idx) {//1024 range, 16 px sprites
        int is = ic.getScaling();
        int sc = getScale();
        int xs = ic.getWidth() / is;
        x *= xs;
        int ys = ic.getHeight() / is;
        y *= ys;
        BufferedImage image = kind.getImage();
        int sx = (frame + idx) * size;
        int sy = (sx / image.getWidth());
        sx -= sy * image.getWidth();
        sy *= size;
        ic.getGraphics()
                .drawImage(image, x, y,
                        x + sc * xs - 1, y + sc * ys - 1,
                        sx, sy,
                        sx + size - 1, sy + size - 1,
                        null);
    }
}
