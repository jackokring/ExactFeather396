package uk.co.kring.ef396.utilities;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.blocks.EnergyBlock;
import uk.co.kring.ef396.blocks.entities.EnergyContainer;
import uk.co.kring.ef396.blocks.entities.EnergyEntity;
import uk.co.kring.ef396.blocks.entities.EnergyScreen;

public class RegistryBlockGroup {

    private RegistryObject<EnergyBlock> block;
    private RegistryObject<BlockEntityType<EnergyEntity>> entity;
    private RegistryObject<MenuType<EnergyContainer>> container;
    private FunkyEntity blockEntitySupplier;
    private FunkyContainer containerSupplier;

    public RegistryBlockGroup(String name, FunkyBlock block,
                              FunkyEntity blockEntitySupplier,
                              // allow following 2 to be null
                              FunkyContainer container,
                              MenuScreens.ScreenConstructor<EnergyContainer,
                                      EnergyScreen> screen) {
        this.block = Registries.blocks.register(name, () -> block.energyBlock(this));
        if(blockEntitySupplier == null) throw new NullPointerException("A block entity supplier is needed.");
        entity = Registries.blockEntities.register(name,
                () -> entityFrom(blockEntitySupplier)
        );
        if(container != null) {
            this.container = Registries.containers.register(name,
                    () -> menuTypeFrom(container));
            if(screen != null) {
                // on client setup so renderer
                ExactFeather.registerRender((event) -> {
                    MenuScreens.register(this.container.get(), screen);
                    // Attach our container to the screen
                });
            }
        }
        this.blockEntitySupplier = blockEntitySupplier;
        this.containerSupplier = container;
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
        return blockEntitySupplier.energyEntity(this, blockPos, blockState);
    }

    public EnergyContainer getFunky(int windowId, Inventory playerInventory, BlockPos pos) {
        return containerSupplier.energyContainer(this, windowId, playerInventory, pos);
    }

    public MenuType<EnergyContainer> menuTypeFrom(FunkyContainer x) {
        return IForgeMenuType.create((windowId, inv, data)
                -> x.energyContainer(this, windowId, inv, data.readBlockPos()));
        // for creating something to register from a funky constructor
    }

    public BlockEntityType<EnergyEntity> entityFrom(FunkyEntity x) {
        return BlockEntityType.Builder.of((pos, state) -> x.energyEntity(this, pos, state),
                this.get().get()).build(null);
        // for creating something to register from a funky constructor
    }


}