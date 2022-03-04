package uk.co.kring.ef396.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.HashMap;

public class BedtimeBook extends WrittenBookItem implements IForgeRegistryEntry<Item> {

    //"item.ef396.book_1": "Book at Bedtime",


    public BedtimeBook(Properties properties, String name) {
        super(properties);
        CompoundTag tag = new CompoundTag();
        loadChapter(name, tag);
        chapters.put(name, new ItemStack(this, 1, tag));
    }

    public static HashMap<String, ItemStack> chapters = new HashMap<>();

    public void loadChapter(String name, CompoundTag tag) {

    }
}
