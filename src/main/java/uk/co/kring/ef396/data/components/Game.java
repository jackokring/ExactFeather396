package uk.co.kring.ef396.data.components;

import uk.co.kring.ef396.data.mini.ImageGeneric;
import uk.co.kring.ef396.data.mini.Keys;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class Game extends Application implements KeyListener {

    public final int scaling = 1024;//virtual resolution
    public final int frameMillis = 50;

    protected boolean running = true;
    protected boolean paused = true;

    public Game() {
        super(null);
        addKeyListener(this);
        Keys.setComboCanBeUsed(false);
    }

    @Override
    public void indirectDrawAll(Graphics g) {
        //basic buffer draw
    }

    public void moveAll(int frames) {
        //move all
    }

    @Override
    public void drawPause(Graphics g) {
        //on pause HUD
    }

    public void doMenuKeys() {
        //if paused intercept cursor etc.
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

    public void process(int frames) {
        if(!paused) {
            moveAll(frames);
        } else {
            doMenuKeys();
        }
        drawAll(paused);
    }

    public void gameLoop() {
        while(running) {
            long ms = System.currentTimeMillis();
            long latest;
            while ((latest = System.currentTimeMillis()) - ms < frameMillis) Thread.yield();
            int frames = (int) (latest - ms) / frameMillis;
            process(frames);
            ms += frames * frameMillis;//update number of frames
            if(Keys.SYS_PAUSE_P.getKey()) togglePaused();
            if(Keys.SYS_ADMIN_ENTER.getKey()) setExit();
        }
    }

    public void setExit() {
        if(!paused) {
            paused = true;
            Keys.setComboCanBeUsed(false);
        } else {
            running = false;
        }
    }

    public void togglePaused() {
        if(!paused) {
            paused = true;
            Keys.setComboCanBeUsed(false);
        } else {
            paused = false;
            Keys.setComboCanBeUsed(true);
        }
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
