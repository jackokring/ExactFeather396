package uk.co.kring.ef396.data.components;

import uk.co.kring.ef396.data.mini.ImageGeneric;
import uk.co.kring.ef396.data.mini.Keys;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class Game extends Application implements KeyListener {

    public final int scaling = 1024;//virtual resolution

    public Game() {
        super(null);
        addKeyListener(this);
    }

    @Override
    public void indirectDrawAll(Graphics g) {
        //basic buffer
    }

    public void drawOne(Graphics g, ImageGeneric ig, int x, int y, int idx) {
        int spriteSize = ig.getScale();
        int virtualX = getWidth() / scaling;
        x *= virtualX;
        int virtualY = getHeight() / scaling;
        y *= virtualY;
        BufferedImage image = ig.getKind().getImage();
        int spriteIndex = (ig.getFrame() + idx) * ig.getSize();
        int spriteDown = (spriteIndex / image.getWidth());
        spriteIndex -= spriteDown * image.getWidth();
        spriteDown *= ig.getSize();
        g.drawImage(image, x, y,
                    x + spriteSize * virtualX - 1, y + spriteSize * virtualY - 1,
                    spriteIndex, spriteDown,
                    spriteIndex + ig.getSize() - 1, spriteDown + ig.getSize() - 1,
                    null);
    }

    public void gameLoop() {

    }

    @Override
    public void whileOpenHalt() {
        gameLoop();
        super.whileOpenHalt();
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        //nothing
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        for (Keys k: Keys.values()) {
            if(k.getKeyCode() == keyEvent.getKeyCode()) k.pressKey();
            break;
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        for (Keys k: Keys.values()) {
            if(k.getKeyCode() == keyEvent.getKeyCode()) k.releaseKey();
            break;
        }
    }
}
