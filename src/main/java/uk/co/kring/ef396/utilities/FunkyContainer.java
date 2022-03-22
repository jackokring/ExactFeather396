package uk.co.kring.ef396.utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import uk.co.kring.ef396.blocks.entities.EnergyContainer;

@FunctionalInterface
public interface FunkyContainer {
    EnergyContainer // funky constructor for EnergyContainer
        energyContainer(RegistryBlockGroup rbg, int windowId, Inventory inventory, BlockPos position);

    /*  The technicality of supporting more than one method requires a
        default throwing a FunctionalCastException() for any actions called
        on not matching the parameterization of the lambda.

        This would then require some type checking of the possible casts.
        Return type would be easy by a getFunctionalClass() method in the
        interface for an if(x.getFunctionalClass() instanceof z.class) for
        obvious return type grouping.

        Having this would ensure the correct return type for assignment
        or a FunctionalCastException. Various non-abstarct adapters might be
        useful to have. But Funky::energyContainer for example might be
        good to have so that the functional templates (a name?) can have
        an equality comparison. A Canonical order for input parameters
        of abstract methods would have to be considered.

        So getFunctionalTemplate() would return Funky::energyContainer for
        example. Method overloading would of course have to be an error as
        in using a functional method reference when the name is not unique.

        Thanks.
    */

}

