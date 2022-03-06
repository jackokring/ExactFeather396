package uk.co.kring.ef396;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import uk.co.kring.ef396.recipes.BrewingCommon;
import uk.co.kring.ef396.utilities.Registries;
import uk.co.kring.ef396.utilities.RegistryMap;

import java.util.LinkedList;
import java.util.function.Consumer;

public class Loaded {

    // for loading up things
    // use static class fields for any references etc

    public static final void init() {
        blocks(Registries.blocks);
        items(Registries.items);
        potions(new BrewingCommon.Brewing());
        LinkedList<Consumer<FMLClientSetupEvent>> render = new LinkedList<>();
        entities(Registries.entities, render);
        render.forEach((reg) -> {
            ExactFeather.registerRender(reg);
        });
        sounds(Registries.sounds);
    }

    public static void blocks(RegistryMap<Block> reg) {

    }

    public static void items(RegistryMap<Item> reg) {

    }

    public static void potions(BrewingCommon.Brewing reg) {

    }

    public static void entities(RegistryMap<EntityType<?>> reg,
                                LinkedList<Consumer<FMLClientSetupEvent>> list) {
    }

    public static void sounds(RegistryMap<SoundEvent> reg) {

    }
}
