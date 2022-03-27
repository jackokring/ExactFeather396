package uk.co.kring.ef396.utilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import uk.co.kring.ef396.ExactFeather;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

public class JsonAdapter {

    JsonObject jo;

    public JsonAdapter() {
        jo = new JsonObject();
    }

    public JsonAdapter(InputStream in) {
        this();
        JsonElement json = null;
        try {
             json = JsonParser.parseString(
                    new String(in.readAllBytes(),
                            StandardCharsets.UTF_8).trim());
        } catch(Exception e) {
            ExactFeather.LOGGER.error(in.getClass() + " has not parsed JSON.");
        }
        if(json != null) jo = json.getAsJsonObject();
    }

    public void forEach(Consumer<Map.Entry<String, JsonElement>> use) {
        jo.entrySet().forEach(use);
    }

    public void merge(InputStream in) {
        JsonAdapter ja = new JsonAdapter(in);
        ja.forEach((entry) -> {
            jo.add(entry.getKey(), entry.getValue());
        });
    }
}
