package uk.co.kring.ef396;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Loader {

    // for loading the Loaded things

    public static final void init() {
        String loaded;
        try {
            loaded = new String(Loader.class.getResourceAsStream("/assets/" +
                            ExactFeather.MOD_ID + "/loaded_load.txt")
                    .readAllBytes(), StandardCharsets.UTF_8).trim();
        } catch(IOException e) {
            loaded =  "";
            ExactFeather.LOGGER.info("No loaded_load.txt file");
        }
        //TODO process class loading from loaded package if instanceof Loaded
    }
}
