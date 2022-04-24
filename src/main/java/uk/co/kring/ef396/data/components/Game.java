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

    public boolean collides(ImageGeneric ig1, int x1, int y1,
                                 ImageGeneric ig2, int x2, int y2, int bound) {//bound flex
        int spriteSize1 = ig1.getScale();
        int spriteSize2 = ig2.getScale();
        int xMin = Math.max(x1, x2) + bound;
        int xMax1 = x1 + spriteSize1;
        int xMax2 = x2 + spriteSize2;
        int xMax = Math.min(xMax1, xMax2) - bound;
        if (xMax > xMin) {
            float yMin = Math.max(y1, y2) + bound;
            float yMax1 = y1 + spriteSize1;
            float yMax2 = y2 + spriteSize2;
            float yMax = Math.min(yMax1, yMax2) - bound;
            if (yMax > yMin) {
                return true;
            }
        }
        return false;
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
