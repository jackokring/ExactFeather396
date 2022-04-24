package uk.co.kring.ef396.data.components;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

public class ImageCanvas extends Component {

    protected BufferedImage background;
    protected int scaling = 1024;

    public int getScaling() {
        return scaling;
    }

    public ImageCanvas(BufferedImage image, String name) {
        background = image;
        setName(name);
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                repaint();
            }

            @Override
            public void componentMoved(ComponentEvent componentEvent) {
                repaint();
            }

            @Override
            public void componentShown(ComponentEvent componentEvent) {
                repaint();
            }

            @Override
            public void componentHidden(ComponentEvent componentEvent) {
                //nothing
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(background, 0, 0, getWidth(), getHeight(),
                0, 0, background.getWidth(), background.getHeight(), null);
    }
}
