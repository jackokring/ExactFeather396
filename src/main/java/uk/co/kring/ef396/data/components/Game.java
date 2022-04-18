package uk.co.kring.ef396.data.components;

public class Game extends Application {

    private static final ExecButton button = new ExecButton("Start", null);

    public Game() {
        super(button);
    }
}
