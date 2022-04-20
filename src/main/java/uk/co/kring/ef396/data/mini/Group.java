package uk.co.kring.ef396.data.mini;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public enum Group {

    PLAYER(null),
    GOODIES(null),
    BADDIES(null),
    GOOD_FIRE(null),
    BAD_FIRE(null),
    FX(null);

    private final BufferedImage image;

    Group(String image) {
        BufferedImage image1;
        try {
            image1 = ImageIO.read(getClass().getResourceAsStream(image));
        } catch (IOException e) {
            image1 = null;
        }
        this.image = image1;
    }

    public BufferedImage getImage() {
        return image;
    }
}
