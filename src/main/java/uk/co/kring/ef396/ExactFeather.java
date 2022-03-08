package uk.co.kring.ef396;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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

    public static boolean DEBUG;

    private static List<Consumer<FMLClientSetupEvent>> renderers = new LinkedList<>();
    private static List<Consumer<EntityAttributeCreationEvent>> attrib = new LinkedList<>();

    public ExactFeather() {

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::setup);
        bus.addListener(this::doClientStuff);
        bus.addListener(this::doAttributes);

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
        MinecraftForge.EVENT_BUS.register(this);

        // build configuration
        Configurator.build();
    }

    private void setConfig(Configurator.Builder builder) {
        builder.comment("Module debug flag");
        DEBUG = builder.readBoolean("debug", false);
    }

    private void setup(final FMLCommonSetupEvent event) {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            renderers.forEach((render) -> {
                render.accept(event);
            });
        });
    }

    private void doAttributes(EntityAttributeCreationEvent event) {
        attrib.forEach((att) -> {
            att.accept(event);
        });
    }

    public static void registerRender(Consumer<FMLClientSetupEvent> event) {
        renderers.add(event);
    }
    public static void registerAttrib(Consumer<EntityAttributeCreationEvent> event) {
        attrib.add(event);
    }

    // Custom CreativeModeTab TAB
    public static final CreativeModeTab TAB = new CreativeModeTab(MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Loaded.hogSpawnEgg.get());
        }
    };
}
