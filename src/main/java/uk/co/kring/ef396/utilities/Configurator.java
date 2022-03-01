package uk.co.kring.ef396.utilities;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.DeferredRegister;
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
    }

    private static final Builder CLIENT = new Builder();
    private static final Builder COMMON = new Builder();
    private static final Builder SERVER = new Builder();

    public static void build() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT.build());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON.build());
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER.build());
        ExactFeather.LOGGER.warn("Configuration built.");
    }

    private static void section(String name, Builder builder, Consumer<Builder> user) {
        builder.push(name);
        user.accept(builder);
        builder.pop();
    }

    public static void configRegistryEntry(DeferredRegister<?> register, String name,
                                           Consumer<Builder> user) {
        section(register.toString() + "." + name, SERVER, user);
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
