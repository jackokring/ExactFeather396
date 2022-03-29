package uk.co.kring.ef396.utilities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import uk.co.kring.ef396.ExactFeather;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

public class JsonAdapter {

    JsonObject jo;

    public JsonAdapter(ExistingFileHelper efh, String prefix,
                       String resource, PackType pack) {
        this();
        try {
            InputStream is = efh.getResource(
                            new ResourceLocation(ExactFeather.MOD_ID, resource),
                            pack, ".json", prefix + "/")
                    .getInputStream();
            load(is);
        } catch(Exception e) {
            ExactFeather.LOGGER.error(this.getClass() + " JSON file error.");
        }
    }

    public JsonAdapter() {
        jo = new JsonObject();
    }

    public JsonAdapter(InputStream in) {
        this();
        load(in);
    }

    public JsonAdapter load(InputStream in) {
        JsonElement json = null;
        try {
            json = JsonParser.parseString(
                    new String(in.readAllBytes(),
                            StandardCharsets.UTF_8).trim());
        } catch(Exception e) {
            ExactFeather.LOGGER.error(in.getClass() + " has bad parsed JSON.");
        }
        if(json != null) jo = json.getAsJsonObject();
        return this;
    }

    public JsonAdapter forEach(Consumer<Map.Entry<String, JsonElement>> use) {
        jo.entrySet().forEach(use);
        return this;
    }

    public JsonAdapter merge(InputStream in) {
        JsonAdapter ja = new JsonAdapter(in);
        ja.forEach((entry) -> {
            jo.add(entry.getKey(), entry.getValue());
        });
        return this;
    }
}
