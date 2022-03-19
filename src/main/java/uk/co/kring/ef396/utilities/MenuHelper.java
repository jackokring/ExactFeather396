package uk.co.kring.ef396.utilities;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;

public class MenuHelper {

    public static MenuType<AbstractContainerMenu> create(FunctionalMenu x) {
        return IForgeMenuType.create((windowId, inv, data)
                -> x.creation(windowId, inv, data.readBlockPos()));//TODO
    }
}
