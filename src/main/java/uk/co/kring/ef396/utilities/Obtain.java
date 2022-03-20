package uk.co.kring.ef396.utilities;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import uk.co.kring.ef396.blocks.entities.EnergyContainer;

public class Obtain {

    public static MenuType<EnergyContainer> menuTypeFrom(Funky x) {
        return IForgeMenuType.create((windowId, inv, data)
                -> x.energyContainer(windowId, inv, data.readBlockPos()));
        // for creating something to register from a funky constructor
    }

}
