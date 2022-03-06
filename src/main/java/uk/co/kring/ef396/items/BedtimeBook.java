package uk.co.kring.ef396.items;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.initializers.ItemInit;
import uk.co.kring.ef396.utilities.Configurator;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Supplier;

public class BedtimeBook extends WrittenBookItem implements IForgeRegistryEntry<Item> {

    //"item.ef396.book_1": "Book at Bedtime",
    private final String name;
    private static HashMap<String, Supplier<ItemStack>> chapters = new HashMap<>();
    private static HashMap<String, String> chapterNames = new HashMap<>();

    public static RegistryObject<Item> register(String name) {
        return ItemInit.ITEMS.register(name, () -> new BedtimeBook(
                new Item.Properties().stacksTo(1)
                        .tab(ExactFeather.TAB),
                chapterNames.get(name)),
                (builder) -> {
                    chapterNames.put(name, builder.readString(name));
                });// this name should be different for file by config
    }

    private BedtimeBook(Properties properties, String name) {
        super(properties);
        CompoundTag tag = new CompoundTag();
        this.name = name;
        loadChapter(tag);
        chapters.put(name, () -> new ItemStack(this, 1, tag));
    }

    private void fillTagWith(CompoundTag tag, String with) {
        //TODO
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> stacks) {
        if(tab != null && !tab.equals(ExactFeather.TAB)) return;
        stacks.add(chapters.get(this.name).get());
        //super.fillItemCategory(tab, stacks);
    }

    private void loadChapter(CompoundTag tag) {
        // needs caching?
        InputStream in = getAsset();
        try {
            fillTagWith(tag, new String(in.readAllBytes(), StandardCharsets.UTF_8));
            in.close();
        } catch(Exception e) {
            // unlikely
            // pages:[{"text":"The Book of Void","color":"red"}],title:Void,author:ef396,generation:3

            // placement default E.G.
            tag.putString("name", "Void");
            tag.putString("title", "Void");
            tag.putString("author", ExactFeather.MOD_ID);
            tag.putInt("generation", 3);

            ListTag lt = new ListTag();
            tag.put("pages", lt);
            CompoundTag page = new CompoundTag();
            lt.add(page);
            page.putString("text", "The Book of Void");
            page.putString("color", "red");
        }
    }

    private InputStream getAsset() {
        return getClass().getResourceAsStream("/assets/" +
                ExactFeather.MOD_ID + "/books/" + name + ".txt");//mappable
    }

    public static ItemStack random(Player player) {
        int size = chapters.size();
        int rnd = player.getRandom().nextInt(size);
        Supplier<ItemStack>[] is = (Supplier<ItemStack>[]) chapters.values().toArray();
        return is[rnd].get();
    }
}
