package uk.co.kring.ef396.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThisLogger {
    public static final Logger LOGGER = LogManager.getLogger();
    private String name;

    public ThisLogger(String name) {
        this.name = name;
    }

    public void info(String say) {
        LOGGER.info(name + ": " + say);
    }
}
