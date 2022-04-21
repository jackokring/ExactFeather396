package uk.co.kring.ef396.data.components;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Application extends Frame {

    protected Component main;

    public Application(Component main) {
        this.main = main;
        add(main);
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
        setVisible(true);
    }

    public void whileOpenHalt() {
        while(isVisible()) Thread.yield();//stay open to show
    }
}
