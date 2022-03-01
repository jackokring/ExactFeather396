package uk.co.kring.ef396;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import uk.co.kring.ef396.initializers.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import uk.co.kring.ef396.utilities.Configurator;
import uk.co.kring.ef396.utilities.ThisLogger;

@Mod("ef396")
public class ExactFeather {

    public static final String MOD_ID = "ef396";
    public static final ThisLogger LOGGER = new ThisLogger(MOD_ID);

    public static ForgeConfigSpec.BooleanValue DEBUG;

    public ExactFeather() {

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::setup);
        bus.addListener(this::doClientStuff);

        // a configuration handler
        // names field etc.
        // look at PoisonAppleItem
        // and its ItemInit entry
        // labelling done so flat for config entries
        // in handler method
        Configurator.configNode("module_global", (builder) -> setConfig(builder));

        BlockInit.BLOCKS.register(bus);
        ItemInit.ITEMS.register(bus);
        BrewInit.POTIONS.register(bus);
        EntityInit.ENTITIES.register(bus);
        SoundInit.SOUNDS.register(bus);

        MinecraftForge.EVENT_BUS.register(this);

        // build configuration
        Configurator.build();
    }

    private void setConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Module debug flag");
        DEBUG = builder.define("debug", false);
    }

    private void setup(final FMLCommonSetupEvent event) {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {

    }

    // Custom CreativeModeTab TAB
    public static final CreativeModeTab TAB = new CreativeModeTab("ef396") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemInit.HOG_SPAWN_EGG.get());
        }
    };
}
