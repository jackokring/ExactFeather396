package uk.co.kring.ef396.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.co.kring.ef396.ExactFeather;

@Mod.EventBusSubscriber(modid = ExactFeather.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class BackendEvents {

    @SubscribeEvent
    public static void onCraftingTableOpen(ServerChatEvent event) {
        // Your Code Here
    }
}
