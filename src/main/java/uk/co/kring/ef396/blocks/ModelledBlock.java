package uk.co.kring.ef396.blocks;

import net.minecraftforge.client.model.generators.BlockStateProvider;

public interface ModelledBlock {

    default void provideModel(BlockStateProvider blockStateProvider) {
        //use to make data generation not make an auto
    }
}
