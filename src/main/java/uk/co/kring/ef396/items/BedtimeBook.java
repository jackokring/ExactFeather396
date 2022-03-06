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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Supplier;

public class BedtimeBook extends WrittenBookItem implements IForgeRegistryEntry<Item> {

    //"item.ef396.book_1": "Book at Bedtime",
    private final String name;

    private static class Entry {
        Supplier<ItemStack> sup;
        String altName;
        String loaded;
        CompoundTag tag = new CompoundTag();

        Entry(String altName) {;
            this.altName = altName;
            try {
                loaded = new String(getClass().getResourceAsStream("/assets/" +
                    ExactFeather.MOD_ID + "/books/" + altName + ".txt")
                    .readAllBytes(), StandardCharsets.UTF_8);
            } catch(IOException e) {
                loaded = "Failed load of /assets/" + ExactFeather.MOD_ID + "/books/" + altName + ".txt";
            }
            loadChapter();
        }

        void loadChapter() {
            // needs caching?

            // TODO
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

        void addSupplier(Supplier<ItemStack> sup) {
            this.sup = sup;
        }
    }

    private static HashMap<String, Entry> chapters = new HashMap<>();

    public static RegistryObject<Item> register(String name) {
        return ItemInit.ITEMS.register(name, () -> new BedtimeBook(
                new Item.Properties().stacksTo(1)
                        .tab(ExactFeather.TAB),
                name),//using name
                (builder) -> {
                    chapters.put(name, new Entry(builder.readString(name)));
                });// this name should be different for file by config
    }

    private BedtimeBook(Properties properties, String name) {
        super(properties);
        this.name = name;
        chapters.get(name).addSupplier(() -> new ItemStack(this, 1,
                chapters.get(name).tag));
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> stacks) {
        if(tab != null && !tab.equals(ExactFeather.TAB)) return;
        stacks.add(chapters.get(name).sup.get());
        //super.fillItemCategory(tab, stacks);
    }

    public static ItemStack random(Player player) {
        int size = chapters.size();
        int rnd = player.getRandom().nextInt(size);
        Supplier<ItemStack>[] is = (Supplier<ItemStack>[]) chapters.values().toArray();
        return is[rnd].get();
    }
}
