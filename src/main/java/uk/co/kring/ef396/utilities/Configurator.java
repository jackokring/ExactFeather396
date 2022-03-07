package uk.co.kring.ef396.utilities;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import uk.co.kring.ef396.ExactFeather;

import java.util.function.Consumer;

public final class Configurator {

    public final static class Builder extends ForgeConfigSpec.Builder {

        public float readFloat(String name, float def, float low, float hi) {
            return this.defineInRange(name, def, low, hi).get().floatValue();
        }

        public boolean readBoolean(String name, boolean def) {
            return this.define(name, def).get();
        }

        public int readInt(String name, int def, int low, int hi) {
            return this.defineInRange(name, def, low, hi).get();
        }

        public String readString(String name) {
            return this.define(name, name).get();
        }
    }

    private static final Builder CLIENT = new Builder();
    private static final Builder COMMON = new Builder();
    private static final Builder SERVER = new Builder();

    private static RegistryMap<?> pushedServer = null;

    public static void build() {
        if(pushedServer != null) SERVER.pop();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT.build());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON.build());
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER.build());
        ExactFeather.LOGGER.info("Configuration built.");
    }

    public static void pushGame(RegistryMap<?> registry) {
        if(registry != pushedServer) {
            if(pushedServer != null) SERVER.pop();//close section
            SERVER.push(registry.toString());
        }
        SERVER.comment("Registry entry:");
        pushedServer = registry;
    }

    private static void section(String name, Builder builder, Consumer<Builder> user) {
        builder.push(name);
        user.accept(builder);
        builder.pop();
    }

    public static void configGame(String name, Consumer<Builder> user) {
        section(name, SERVER, user);//single edit unified config (not player override)
    }

    public static void configTart(String name, Consumer<Builder> user) {
        section(name, CLIENT, user);//per user edit config
    }

    public static void configNode(String name, Consumer<Builder> user) {
        section(name, COMMON, user);//network node config
    }
}
