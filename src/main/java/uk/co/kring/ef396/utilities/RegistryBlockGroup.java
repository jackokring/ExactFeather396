package uk.co.kring.ef396.utilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.blocks.EnergyBlock;
import uk.co.kring.ef396.blocks.entities.EnergyContainer;
import uk.co.kring.ef396.blocks.entities.EnergyEntity;

public class RegistryBlockGroup {

    private RegistryObject<EnergyBlock> block;
    private RegistryObject<BlockEntityType<EnergyEntity>> entity;
    private RegistryObject<MenuType<EnergyContainer>> container;
    private BlockEntityType.BlockEntitySupplier<EnergyEntity> blockEntitySupplier;
    private Funky containerSupplier;

    public RegistryBlockGroup(RegistryObject<EnergyBlock> block,
                              RegistryObject<BlockEntityType<EnergyEntity>> entity,
                              RegistryObject<MenuType<EnergyContainer>> container,
                              BlockEntityType.BlockEntitySupplier<EnergyEntity>
                                      blockEntitySupplier,
                              Funky containerSupplier) {
        this.block = block;
        this.entity = entity;
        this.container = container;
        this.blockEntitySupplier = blockEntitySupplier;
        this.containerSupplier = containerSupplier;
    }

    public RegistryObject<EnergyBlock> get() {
        return block;
    }

    public RegistryObject<BlockEntityType<EnergyEntity>> getEntity() {
        return entity;
    }

    public RegistryObject<MenuType<EnergyContainer>> getContainer() {
        return container;
    }

    public EnergyEntity getEntity(BlockPos blockPos, BlockState blockState) {
        return blockEntitySupplier.create(blockPos, blockState);
    }

    public EnergyContainer getFunky(int windowId, Inventory playerInventory, BlockPos pos) {
        return containerSupplier.energyContainer(windowId, playerInventory, pos);
    }
}