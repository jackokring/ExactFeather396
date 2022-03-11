package uk.co.kring.ef396;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Loader {

    // for loading the Loaded things
    private static List<Optional<?>> classes;

    public static final List<Optional<?>> getLoaded() {
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
                    Class<?> c = null;
                    try {
                        c = classLoader.loadClass(clazz);
                        var i = c.getConstructor().newInstance();
                        //c.getDeclaredMethod("init", Loaded.class).invoke(null, i);
                        //static action ... eye, eye cap'n!
                        // uses instance for overrides
                    } catch(ClassNotFoundException e) {
                        // didn't load
                        message(c, "Can't find");
                    } catch(NoSuchMethodException e) {
                        // didn't load
                        message(c, "Can't find default constructor for");
                    } catch(InvocationTargetException e) {
                        // had problems
                        message(c, "An error in");
                        message(c, e.getCause().toString());//print exception
                    } catch(InstantiationException e) {
                        // didn't load
                        message(c, "Couldn't make an instance of");
                    } catch(IllegalAccessException e) {
                        // didn't load
                        message(c, "No public default constructor in");
                    }
                    return Optional.ofNullable(c);
                }).collect(Collectors.toList());
    }

    private static void message(Class<?> from, String say) {
        ExactFeather.LOGGER.error(say + " " + from.getCanonicalName());
    }
}
