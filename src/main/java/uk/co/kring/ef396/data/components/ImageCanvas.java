package uk.co.kring.ef396.data.components;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

public class ImageCanvas extends Component {

    protected BufferedImage background;
    protected boolean lazy = false;
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
                draw();
            }

            @Override
            public void componentMoved(ComponentEvent componentEvent) {
                draw();
            }

            @Override
            public void componentShown(ComponentEvent componentEvent) {
                lazy = false;
                draw();
            }

            @Override
            public void componentHidden(ComponentEvent componentEvent) {
                lazy = true;
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(background, 0, 0, getWidth(), getHeight(),
                0, 0, background.getWidth(), background.getHeight(), null);
    }

    public final void draw() {
        Graphics g = getGraphics();
        if(g != null && !lazy) {
            paint(g);
            g.dispose();
            getToolkit().sync();
        }
    }
}
