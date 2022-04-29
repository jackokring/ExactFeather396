package uk.co.kring.ef396.data.components;

import uk.co.kring.ef396.data.mini.GameState;
import uk.co.kring.ef396.data.mini.Group;
import uk.co.kring.ef396.data.mini.ImageGeneric;
import uk.co.kring.ef396.data.mini.Keys;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class Game extends Application implements KeyListener {

    public final int scaling = 1024;//virtual resolution
    public final int frameMillis = 50;

    protected boolean running = true;
    protected boolean paused = true;
    protected GameState state = GameState.INIT;
    private Class<LinkedList<Sprite>[]> spec;
    protected final LinkedList<Sprite>[] displayList
            = (LinkedList<Sprite>[]) new Object[Group.values().length];

    public Game() {
        super(null);
        for(int i = 0; i < displayList.length; i++) {
            displayList[i] = new LinkedList<>();
        }
        addKeyListener(this);
        Keys.setComboCanBeUsed(false);
    }

    public void setGameState(GameState state) {
        this.state = state;
    }

    public GameState getGameState() {
        return state;
    }

    private GameState processGameState(GameState gameState) {
        return gameState;
    }

    @Override
    public void indirectDrawAll(Graphics g) {
        //basic buffer draw
        for(int i = 0; i < Group.values().length; i++) {
            for(Sprite s: displayList[i]) {
                drawOne(g, s);
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        //nothing
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

    public void drawOne(Graphics g, Sprite sprite) {
        ImageGeneric ig = sprite.getImageGeneric();
        int spriteSize = ig.getScale();
        int virtualX = getWidth() / scaling;
        int x = sprite.getX();
        x *= virtualX;
        int virtualY = getHeight() / scaling;
        int y = sprite.getY();
        y *= virtualY;
        BufferedImage image = ig.getKind().getImage();
        int spriteIndex = sprite.getIdx() * ig.getSize();
        int spriteDown = (spriteIndex / image.getWidth());
        spriteIndex -= spriteDown * image.getWidth();
        spriteDown *= ig.getSize();
        g.drawImage(image, x, y,
                    x + spriteSize * virtualX - 1, y + spriteSize * virtualY - 1,
                    spriteIndex, spriteDown,
                    spriteIndex + ig.getSize() - 1, spriteDown + ig.getSize() - 1,
                    null);
    }

    public boolean collides(Sprite sprite1, Sprite sprite2) {//bound flex
        int spriteSize1 = sprite1.getImageGeneric().getScale();
        int spriteSize2 = sprite2.getImageGeneric().getScale();
        int bound = sprite1.getBound() + sprite2.getBound();
        int xMin = Math.max(sprite1.getX(), sprite2.getX()) + bound;
        int xMax1 = sprite1.getX() + spriteSize1;
        int xMax2 = sprite2.getX() + spriteSize2;
        int xMax = Math.min(xMax1, xMax2) - bound;
        if (xMax > xMin) {
            float yMin = Math.max(sprite1.getY(), sprite2.getY()) + bound;
            float yMax1 = sprite1.getY() + spriteSize1;
            float yMax2 = sprite2.getY() + spriteSize2;
            float yMax = Math.min(yMax1, yMax2) - bound;
            if (yMax > yMin) {
                return true;
            }
        }
        return false;
    }

    public void collide() {
        for(Group.Collide col: Group.Collide.values()) {
            for(Sprite s: displayList[col.what().ordinal()]) {
                for(Group group: col.with()) {
                    for (Sprite p : displayList[group.ordinal()]) {
                        if(collides(s, p) && s.collided(p, col)) {
                            //requested removal
                            //TODO
                        }
                    }
                }
            }
        }
    }

    public void process(int frames) {
        if(!paused) {
            moveAll(frames);
        } else {
            doMenuKeys();
        }
        drawAll(paused);
        collide();
        setGameState(processGameState(getGameState()));//state machine
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
