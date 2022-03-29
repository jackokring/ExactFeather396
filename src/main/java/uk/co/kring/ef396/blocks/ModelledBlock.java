package uk.co.kring.ef396.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;

public interface ModelledBlock {

    default void provideModel(BlockStateProvider bsp, Block b) {
        //use to make data generation not make an auto
        bsp.simpleBlock(b);
    }
}
