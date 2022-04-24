package uk.co.kring.ef396.data.components;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

public class Application extends Frame {

    protected Component main;
    protected BufferStrategy bs;

    public Application(Component main) {
        if(main != null) {
            this.main = main;
            add(main);
            setTitle(main.getName());
        }
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                setVisible(false);
            }
        });
        setTitle(main.getName());
        try {
            setIconImage(ImageIO.read(getClass().getResourceAsStream("ef396.png")));
        } catch(Exception e) {
            // no image found
        }
        createBufferStrategy(3);
        bs = getBufferStrategy();
        setVisible(true);
    }

    public void whileOpenHalt() {
        while(isVisible()) Thread.yield();//stay open to show
        bs.dispose();
        dispose();
    }

    public void indirectDraw(Graphics g) {
        paint(g);
    }

    public void draw() {
        Graphics g = bs.getDrawGraphics();
        do {
            if(g != null) {
                indirectDraw(g);
                g.dispose();
                bs.show();
            }
        } while (bs.contentsLost());
    }
}
