package uk.co.kring.ef396;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Loader {

    // for loading the Loaded things
    public static List<Optional> classes;

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
                    try {
                        return Optional.of(classLoader.loadClass(clazz));//loaded classes
                    } catch(Exception e) {
                        return Optional.empty();
                    }
                }).collect(Collectors.toList());
    }
}
