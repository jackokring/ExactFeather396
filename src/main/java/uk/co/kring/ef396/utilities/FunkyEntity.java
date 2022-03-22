package uk.co.kring.ef396.utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import uk.co.kring.ef396.blocks.entities.EnergyEntity;

@FunctionalInterface
public interface FunkyEntity {

    EnergyEntity // funky constructor for EnergyEntity
        energyEntity(RegistryBlockGroup rbg, BlockPos pos, BlockState state);
}
