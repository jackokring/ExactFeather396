package uk.co.kring.ef396.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.OreBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class RubyOre extends OreBlock {

    public RubyOre() {
        super(Block.Properties.of(Material.METAL)
                .requiresCorrectToolForDrops()
                .strength(3.0f, 4.0f)
                .sound(SoundType.STONE));
    }

    @Override
    public int getExpDrop(BlockState state, LevelReader reader, BlockPos pos, int fortune, int silktouch) {
        return 1;
    }


}
