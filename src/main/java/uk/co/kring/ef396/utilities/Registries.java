package uk.co.kring.ef396.utilities;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import uk.co.kring.ef396.ExactFeather;

public class Registries {

    public final static RegistryMap<Item> items
            = new RegistryMap<Item>(DeferredRegister.create(ForgeRegistries.ITEMS, ExactFeather.MOD_ID));
    public final static RegistryMap<Block> blocks
            = new RegistryMap<Block>(DeferredRegister.create(ForgeRegistries.BLOCKS, ExactFeather.MOD_ID));;
    public final static RegistryMap<Potion> potions
            = new RegistryMap<Potion>(DeferredRegister.create(ForgeRegistries.POTIONS, ExactFeather.MOD_ID));
    public final static RegistryMap<EntityType<?>> entities
            = new RegistryMap<>(DeferredRegister.create(ForgeRegistries.ENTITIES, ExactFeather.MOD_ID));
    public final static RegistryMap<SoundEvent> sounds
            = new RegistryMap<SoundEvent>(DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ExactFeather.MOD_ID));

    public static void register(IEventBus bus) {
        items.register(bus);
        blocks.register(bus);
        potions.register(bus);
        entities.register(bus);
        sounds.register(bus);
    }
}
