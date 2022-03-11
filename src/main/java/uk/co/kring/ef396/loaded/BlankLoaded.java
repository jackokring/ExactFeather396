package uk.co.kring.ef396.loaded;

import net.minecraft.sounds.SoundEvent;;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.Loaded;
import uk.co.kring.ef396.utilities.RegistryMap;

public class BlankLoaded extends Loaded {

    // use public statics for easy access in other modules of data fields
    public static RegistryObject<? extends Item> a;
    public static RegistryObject<Block> b;
    public static RegistryObject<Potion> c;
    public static RegistryObject<EntityType<? extends Mob>> d;
    public static RegistryObject<SoundEvent> e;

    // use dynamics
    @Override
    protected void items(RegistryMap<Item> reg) {

    }

    @Override
    protected void potions(RegistryMap<Potion> reg) {

    }

    @Override
    protected void blocks(RegistryMap<Block> reg) {

    }

    @Override
    protected void entities(RegistryMap<EntityType<?>> reg) {

    }

    @Override
    protected void sounds(RegistryMap<SoundEvent> reg) {

    }
}
