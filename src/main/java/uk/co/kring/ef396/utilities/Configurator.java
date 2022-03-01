package uk.co.kring.ef396.utilities;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.DeferredRegister;
import uk.co.kring.ef396.ExactFeather;

import java.util.function.Consumer;

public final class Configurator {

    private static final ForgeConfigSpec.Builder CLIENT = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder COMMON = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.Builder SERVER = new ForgeConfigSpec.Builder();

    public static void build() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT.build());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON.build());
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER.build());
        ExactFeather.LOGGER.warn("Configuration built.");
    }

    private static void section(String name, ForgeConfigSpec.Builder builder,
                               Consumer<ForgeConfigSpec.Builder> user) {
        builder.push(name);
        user.accept(builder);
        builder.pop();
    }

    public static void configRegistryEntry(DeferredRegister<?> register, String name,
                                           Consumer<ForgeConfigSpec.Builder> user) {
        section(register.toString() + "." + name, SERVER, user);
    }

    public static void configGame(String name, Consumer<ForgeConfigSpec.Builder> user) {
        section(name, SERVER, user);//single edit unified config (not player override)
    }

    public static void configTart(String name, Consumer<ForgeConfigSpec.Builder> user) {
        section(name, CLIENT, user);//per user edit config
    }

    public static void configNode(String name, Consumer<ForgeConfigSpec.Builder> user) {
        section(name, COMMON, user);//network node config
    }
}
