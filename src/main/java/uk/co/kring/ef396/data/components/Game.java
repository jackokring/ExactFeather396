package uk.co.kring.ef396.data.components;

import java.awt.*;

public class Game extends Application {
    public Game() {
        super(null);
    }

    @Override
    public void indirectDraw(Graphics g) {
        //basic buffer
    }

    public void gameLoop() {

    }

    @Override
    public void whileOpenHalt() {
        gameLoop();
        super.whileOpenHalt();
    }
}
