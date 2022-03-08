package uk.co.kring.ef396;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Loader {

    // for loading the Loaded things
    private static List<Optional<Class<Loaded>>> classes;

    public static final List<Optional<Class<Loaded>>> getLoaded() {
        return Collections.unmodifiableList(classes);
    }

    public static final void init(ClassLoader classLoader) {
        String loaded;
        try {
            loaded = new String(Loader.class.getResourceAsStream("/assets/" +
                            ExactFeather.MOD_ID + "/loaded_load.txt")
                    .readAllBytes(), StandardCharsets.UTF_8).trim();
        } catch(IOException e) {
            loaded =  "";
            ExactFeather.LOGGER.info("No loaded_load.txt file");
        }
        classes = Arrays.stream(loaded.split("\n"))
                .map((string) -> string.trim())
                .filter((string) -> string.charAt(0) != '#')//not comment
                .map((string) -> Loader.class.getPackageName() + "." + string.trim())
                .map((clazz) -> {
                    Class<Loaded> c = null;
                    try {
                        c = (Class<Loaded>)classLoader.loadClass(clazz);
                        var i = c.getConstructor().newInstance();
                        c.getMethod("init").invoke(i, i);//static action ... eye, eye cap'n!
                        // uses instance for overrides
                    } catch(Exception e) {
                        // didn't load
                        ExactFeather.LOGGER.info("Class " + clazz + " didn't load.");
                    }
                    return Optional.ofNullable(c);
                }).collect(Collectors.toList());
    }
}
