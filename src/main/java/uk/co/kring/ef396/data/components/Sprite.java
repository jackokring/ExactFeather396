package uk.co.kring.ef396.data.components;

import uk.co.kring.ef396.data.mini.Group;
import uk.co.kring.ef396.data.mini.ImageGeneric;

public class Sprite {

    public static class Velocity {

    }

    private final ImageGeneric ig;
    private int x, y;
    private int idx;
    private Velocity v;

    public Sprite(ImageGeneric ig, int x, int y, int idx, Velocity v) {
        this.ig = ig;
        this.x = x;
        this.y = y;
        this.idx = idx;
        this.v = v;
    }

    public int getBound() {
        return ig.getBound(idx);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ImageGeneric getImageGeneric() {
        return ig;
    }

    public Velocity getVelocity() {
        return v;
    }

    public int getIdx() {
        return idx;
    }

    public boolean collided(Sprite with, Group.Collide groupCollision) {
        return false;//TODO return true for remove from iterator
    }
}
