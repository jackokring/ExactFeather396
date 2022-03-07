package uk.co.kring.ef396.server;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.Loaded;
import uk.co.kring.ef396.items.BedtimeBook;
import uk.co.kring.ef396.utilities.ThisLogger;

@Mod.EventBusSubscriber(modid = ExactFeather.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class LevelController {

    @SubscribeEvent
    public static void onPlayerMade(PlayerSleepInBedEvent event) {
        if(event.getPlayer().getInventory().add(BedtimeBook.random(event.getPlayer()))) {
            ThisLogger.LOGGER.info("A nice book for " + event.getPlayer().getName().getContents() + " to read?");
        }
    }

    @SubscribeEvent
    public static void missingItemMappings(final RegistryEvent.MissingMappings<Item> event) {
        event.getAllMappings().forEach(missing -> {
            ThisLogger.LOGGER.info("\"" + missing.key + "\" is a missing registry item mapping! Enjoy an apple.");
            missing.remap(Loaded.poison_apple.get());
        });//debug missing mappings
    }

    @SubscribeEvent
    public static void missingBlockMappings(final RegistryEvent.MissingMappings<Block> event) {
        event.getAllMappings().forEach(missing -> {
            ThisLogger.LOGGER.info("\"" + missing.key + "\" is a missing registry block mapping! Some ruby perhaps?");
            missing.remap(Loaded.rubyBlock.get());
        });//debug missing mappings
    }

    @SubscribeEvent
    public static void missingEntityMappings(final RegistryEvent.MissingMappings<EntityType<?>> event) {
        event.getAllMappings().forEach(missing -> {
            ThisLogger.LOGGER.info("\"" + missing.key + "\" is a missing registry entity mapping! Oh, no!");
            missing.remap(Loaded.hog.get());
        });//debug missing mappings
    }

    @SubscribeEvent
    public static void missingPotionMappings(final RegistryEvent.MissingMappings<Potion> event) {
        event.getAllMappings().forEach(missing -> {
            ThisLogger.LOGGER.info("\"" + missing.key + "\" is a missing registry entity mapping! Oh, no!");
            missing.remap(Loaded.psydare.get());
        });//debug missing mappings
    }

    @SubscribeEvent
    public static void missingSoundMappings(final RegistryEvent.MissingMappings<SoundEvent> event) {
        event.getAllMappings().forEach(missing -> {
            ThisLogger.LOGGER.info("\"" + missing.key + "\" is a missing registry sound mapping! Alarm!");
            missing.remap(Loaded.error.get());
        });//debug missing mappings
    }
}
