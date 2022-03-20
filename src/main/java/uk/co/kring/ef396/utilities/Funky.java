package uk.co.kring.ef396.utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import uk.co.kring.ef396.blocks.entities.EnergyContainer;

@FunctionalInterface
public interface Funky {
    EnergyContainer // funky constructor for EnergyContainer
        energyContainer(int windowId, Inventory inventory, BlockPos position);

}

