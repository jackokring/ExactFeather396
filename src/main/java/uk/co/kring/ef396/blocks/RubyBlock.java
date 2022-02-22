package uk.co.kring.ef396.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class RubyBlock extends Block {

    public RubyBlock() {
        super(Block.Properties.of(Material.METAL)
                .requiresCorrectToolForDrops()
                .strength(5.0f, 6.0f)
                .sound(SoundType.METAL));
    }
}
