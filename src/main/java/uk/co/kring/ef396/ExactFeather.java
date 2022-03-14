package uk.co.kring.ef396;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import uk.co.kring.ef396.server.LevelController;
import uk.co.kring.ef396.utilities.Configurator;
import uk.co.kring.ef396.utilities.Registries;
import uk.co.kring.ef396.utilities.ThisLogger;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

@Mod("ef396")
public class ExactFeather {

    public static final String MOD_ID = "ef396";
    public static final ThisLogger LOGGER = new ThisLogger(MOD_ID);

    //static hook avoiding GC
    public static final ClassLoader LOADER = ClassLoader.getSystemClassLoader();

    public static ForgeConfigSpec.BooleanValue DEBUG;

    private static List<Consumer<FMLClientSetupEvent>> renderers = new LinkedList<>();
    private static List<Consumer<FMLCommonSetupEvent>> recipes = new LinkedList<>();
    private static List<Consumer<EntityAttributeCreationEvent>> attrib = new LinkedList<>();
    private static List<Consumer<BiomeLoadingEvent>> spawn = new LinkedList<>();

    public ExactFeather() {

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        // also check server.LevelController
        bus.addListener(this::setup);
        bus.addListener(this::doClientStuff);
        bus.addListener(this::doAttributes);
        bus.addListener(this::doSpawn);
        MinecraftForge.EVENT_BUS.register(this);

        // a configuration handler
        // names field etc.
        // look at PoisonAppleItem
        // and its ItemInit entry
        // labelling done so flat for config entries
        // in handler method
        Configurator.configNode("module_global",
                (builder) -> setConfig(builder));

        Registries.register(bus);
        Loader.init(LOADER);
        LevelController.init(bus);
        // build configuration
        Configurator.build();
    }

    private void setConfig(Configurator.Builder builder) {
        builder.comment("Module debug flag");
        DEBUG = builder.readBoolean("debug", false);
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            recipes.forEach((recipe) -> {
                recipe.accept(event);
            });
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            renderers.forEach((render) -> {
                render.accept(event);
            });
        });
    }

    private void doAttributes(final EntityAttributeCreationEvent event) {
        attrib.forEach((att) -> {
            att.accept(event);
        });
    }

    private void doSpawn(final BiomeLoadingEvent event) {
        spawn.forEach((spawnEvent) -> {
            spawnEvent.accept(event);
        });
    }

    public static final void registerRender(Consumer<FMLClientSetupEvent> event) {
        renderers.add(event);
    }
    public static final void registerRecipe(Consumer<FMLCommonSetupEvent> event) {
        recipes.add(event);
    }
    public static final void registerAttrib(Consumer<EntityAttributeCreationEvent> event) {
        attrib.add(event);
    }
    public static final void registerSpawn(Consumer<BiomeLoadingEvent> event) { spawn.add(event); }
}
