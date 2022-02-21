package uk.co.kring.ef396;

import net.minecraft.world.item.ItemStack;
import uk.co.kring.ef396.entities.HogEntity;
import uk.co.kring.ef396.init.ModBlocks;
import uk.co.kring.ef396.init.ModEntityType;
import uk.co.kring.ef396.init.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("ef396")
public class ExactFeather {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "tutorial";

    public ExactFeather() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        ModBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModEntityType.ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        DeferredWorkQueue.runLater(() -> {
            GlobalEntityTypeAttributes.put(ModEntityType.HOG.get(), HogEntity.setCustomAttributes().create());
        });
    }

    private void doClientStuff(final FMLClientSetupEvent event) { }

    // Custom ItemGroup TAB
    public static final ItemGroup TAB = new ItemGroup("ef396itemgroup") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.RUBY.get());
        }
    };

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
}
