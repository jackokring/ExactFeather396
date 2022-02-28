package uk.co.kring.ef396.utilities;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import uk.co.kring.ef396.ExactFeather;

import java.util.function.Consumer;

public final class Configurator {

    public static final ForgeConfigSpec.Builder CLIENT = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.Builder COMMON = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.Builder SERVER = new ForgeConfigSpec.Builder();

    public static void build() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT.build());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON.build());
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER.build());
        ExactFeather.LOGGER.warn("Configuration built.");
    }

    public static void section(String name, ForgeConfigSpec.Builder builder,
                               Consumer<ForgeConfigSpec.Builder> user) {
        builder.push(name).comment("Global configurator setting");
        user.accept(builder);
        builder.pop();
    }
}
