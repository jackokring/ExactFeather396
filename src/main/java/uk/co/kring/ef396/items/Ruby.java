package uk.co.kring.ef396.items;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import uk.co.kring.ef396.tags.GemTag;

public class Ruby extends Item implements GemTag {

    public Ruby(CreativeModeTab tab) {
        super(new Item.Properties().tab(tab));
    }
}
