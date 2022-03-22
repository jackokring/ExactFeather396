package uk.co.kring.ef396.utilities;

import uk.co.kring.ef396.blocks.EnergyBlock;
import java.util.function.Supplier;

@FunctionalInterface
public interface FunkyBlock {

    EnergyBlock // funky constructor for EnergyBlock
        energyBlock(RegistryBlockGroup rbg);
}
