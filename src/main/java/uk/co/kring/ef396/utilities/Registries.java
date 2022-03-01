package uk.co.kring.ef396.utilities;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import uk.co.kring.ef396.initializers.*;

public class Registries {

    public final static RegistryMap<Block> blocks = BlockInit.BLOCKS;
    public final static RegistryMap<Item> items = ItemInit.ITEMS;
    public final static RegistryMap<Potion> potions = BrewInit.POTIONS;
    public final static RegistryMap<EntityType<?>> entities = EntityInit.ENTITIES;
    public final static RegistryMap<SoundEvent> sounds = SoundInit.SOUNDS;

    public static void register(IEventBus bus) {
        blocks.register(bus);
        items.register(bus);
        potions.register(bus);
        entities.register(bus);
        sounds.register(bus);
    }
}
