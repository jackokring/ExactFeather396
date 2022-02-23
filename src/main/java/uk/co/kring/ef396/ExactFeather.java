package uk.co.kring.ef396;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import uk.co.kring.ef396.init.BlocksInit;
import uk.co.kring.ef396.init.EntityInit;
import uk.co.kring.ef396.init.ItemsInit;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.co.kring.ef396.init.SoundInit;

@Mod("ef396")
public class ExactFeather {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "ef396";

    public ExactFeather() {

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::setup);
        bus.addListener(this::doClientStuff);

        BlocksInit.BLOCKS.register(bus);
        ItemsInit.ITEMS.register(bus);
        EntityInit.ENTITY_TYPES.register(bus);
        SoundInit.SOUNDS.register(bus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {

    }

    // Custom CreativeModeTab TAB
    public static final CreativeModeTab TAB = new CreativeModeTab("ef396Tab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemsInit.RUBY.get());
        }
    };
}
