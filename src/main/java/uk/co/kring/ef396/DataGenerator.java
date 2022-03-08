package uk.co.kring.ef396;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = "ef396", bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerator {

    @SubscribeEvent
    public static void genData(GatherDataEvent event) {
        if(event.includeServer()) includeServer(event);
        if(event.includeClient()) includeClient(event);
    }

    private static void includeServer(GatherDataEvent event) {

    }

    private static void includeClient(GatherDataEvent event) {

    }
}
