package uk.co.kring.ef396.utilities;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import uk.co.kring.ef396.blocks.entities.EnergyContainer;
import uk.co.kring.ef396.blocks.entities.EnergyEntity;

public class Obtain {

    public static MenuType<EnergyContainer> menuTypeFrom(FunkyContainer x, RegistryBlockGroup rbg) {
        return IForgeMenuType.create((windowId, inv, data)
                -> x.energyContainer(rbg, windowId, inv, data.readBlockPos()));
        // for creating something to register from a funky constructor
    }

    public static BlockEntityType<EnergyEntity> entityFrom(FunkyEntity x, RegistryBlockGroup rbg) {
        return BlockEntityType.Builder.of((pos, state) -> x.energyEntity(rbg, pos, state),
                rbg.get().get()).build(null);
        // for creating something to register from a funky constructor
    }


}
