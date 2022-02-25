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
import uk.co.kring.ef396.initializers.BlockInit;
import uk.co.kring.ef396.initializers.EntityInit;
import uk.co.kring.ef396.initializers.ItemInit;
import uk.co.kring.ef396.initializers.SoundInit;

@Mod.EventBusSubscriber(modid = ExactFeather.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class LevelController {

    @SubscribeEvent
    public static void onCraftingTableOpen(ServerChatEvent event) {
        // Your Code Here
    }

    @SubscribeEvent
    public static void missingItemMappings(final RegistryEvent.MissingMappings<Item> event) {
        event.getAllMappings().forEach(missing -> {
            ExactFeather.LOGGER.warn("\"" + missing.key + "\" is a missing registry item mapping! Enjoy an apple.");
            missing.remap(ItemInit.POISON_APPLE.get());
        });//debug missing mappings
    }

    @SubscribeEvent
    public static void missingBlockMappings(final RegistryEvent.MissingMappings<Block> event) {
        event.getAllMappings().forEach(missing -> {
            ExactFeather.LOGGER.warn("\"" + missing.key + "\" is a missing registry block mapping! Some ruby perhaps?");
            missing.remap(BlockInit.RUBY_BLOCK.get());
        });//debug missing mappings
    }

    @SubscribeEvent
    public static void missingEntityMappings(final RegistryEvent.MissingMappings<EntityType<?>> event) {
        event.getAllMappings().forEach(missing -> {
            ExactFeather.LOGGER.warn("\"" + missing.key + "\" is a missing registry entity mapping! Oh, no!");
            missing.remap(EntityInit.HOG.get());
        });//debug missing mappings
    }

    @SubscribeEvent
    public static void missingSoundMappings(final RegistryEvent.MissingMappings<SoundEvent> event) {
        event.getAllMappings().forEach(missing -> {
            ExactFeather.LOGGER.warn("\"" + missing.key + "\" is a missing registry sound mapping! Alarm!");
            missing.remap(SoundInit.ERROR.get());
        });//debug missing mappings
    }
}
