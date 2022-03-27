package uk.co.kring.ef396.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import uk.co.kring.ef396.tags.MinablePickaxe;
import uk.co.kring.ef396.tags.MinableStone;

public class RubyBlock extends Block implements
        MinablePickaxe, MinableStone {

    public RubyBlock() {
        super(Block.Properties.of(Material.STONE)
                .requiresCorrectToolForDrops()
                .strength(5.0f, 6.0f)
                .sound(SoundType.STONE));
    }
}
