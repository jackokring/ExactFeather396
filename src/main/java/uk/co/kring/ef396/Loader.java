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
            ExactFeather.LOGGER.warn("No loaded_load.txt file. Loading skipped.");
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
                        c.getMethod("init").invoke(null, i);//static action ... eye, eye cap'n!
                        // uses instance for overrides
                    } catch(Exception e) {
                        // didn't load
                        ExactFeather.LOGGER.warn("Class " + clazz +
                                " didn't load. I hope you like Exception messages.");
                    }
                    return Optional.ofNullable(c);
                }).collect(Collectors.toList());
    }
}
