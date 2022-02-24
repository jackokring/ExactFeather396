package uk.co.kring.ef396.server;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.co.kring.ef396.ExactFeather;

@Mod.EventBusSubscriber(modid = ExactFeather.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class LevelController {

    @SubscribeEvent
    public static void onCraftingTableOpen(ServerChatEvent event) {
        // Your Code Here
    }

    @SubscribeEvent
    public static void missingItemMappings(final RegistryEvent.MissingMappings<Item> event) {
        event.getAllMappings().forEach(missing -> {
            ExactFeather.LOGGER.warn("\"" + missing.key + "\" is a missing registry item mapping!");
            missing.ignore();
        });//debug missing mappings
    }

    @SubscribeEvent
    public static void missingBlockMappings(final RegistryEvent.MissingMappings<Block> event) {
        event.getAllMappings().forEach(missing -> {
            ExactFeather.LOGGER.warn("\"" + missing.key + "\" is a missing registry block mapping!");
            missing.ignore();
        });//debug missing mappings
    }

    @SubscribeEvent
    public static void missingEntityMappings(final RegistryEvent.MissingMappings<EntityType<?>> event) {
        event.getAllMappings().forEach(missing -> {
            ExactFeather.LOGGER.warn("\"" + missing.key + "\" is a missing registry entity mapping!");
            missing.ignore();
        });//debug missing mappings
    }

    @SubscribeEvent
    public static void missingSoundMappings(final RegistryEvent.MissingMappings<SoundEvent> event) {
        event.getAllMappings().forEach(missing -> {
            ExactFeather.LOGGER.warn("\"" + missing.key + "\" is a missing registry sound mapping!");
            missing.ignore();
        });//debug missing mappings
    }
}
