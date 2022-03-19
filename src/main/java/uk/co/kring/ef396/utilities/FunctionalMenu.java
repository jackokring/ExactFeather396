package uk.co.kring.ef396.utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

@FunctionalInterface
public interface FunctionalMenu {
    MenuType<AbstractContainerMenu> creation(int windowId, Inventory inventory, BlockPos position);
}

