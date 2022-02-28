package uk.co.kring.ef396;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import uk.co.kring.ef396.initializers.BlockInit;
import uk.co.kring.ef396.initializers.EntityInit;
import uk.co.kring.ef396.initializers.ItemInit;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.kring.ef396.initializers.SoundInit;
import uk.co.kring.ef396.items.enums.ModArmorMaterial;
import uk.co.kring.ef396.utilities.Configurator;

@Mod("ef396")
public class ExactFeather {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "ef396";

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
        Configurator.section("module_global", Configurator.COMMON,
                (builder) -> setConfig(builder));

        BlockInit.BLOCKS.register(bus);
        ItemInit.ITEMS.register(bus);
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
