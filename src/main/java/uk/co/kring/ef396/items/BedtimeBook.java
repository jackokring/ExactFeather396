package uk.co.kring.ef396.items;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.initializers.ItemInit;

import java.io.InputStream;
import java.util.HashMap;
import java.util.function.Supplier;

public class BedtimeBook extends WrittenBookItem implements IForgeRegistryEntry<Item> {

    //"item.ef396.book_1": "Book at Bedtime",
    private final String name;

    public static RegistryObject<Item> register(String name) {
        return ItemInit.ITEMS.register(name, () -> new BedtimeBook(
                new Item.Properties().stacksTo(1)
                        .tab(ExactFeather.TAB),
                name));
    }

    public BedtimeBook(Properties properties, String name) {
        super(properties);
        CompoundTag tag = new CompoundTag();
        this.name = name;
        loadChapter(tag);
        chapters.put(name, () -> new ItemStack(this, 1, tag));
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> stacks) {
        if(tab != null && !tab.equals(ExactFeather.TAB)) return;
        stacks.add(chapters.get(this.name).get());
        //super.fillItemCategory(tab, stacks);
    }

    public static HashMap<String, Supplier<ItemStack>> chapters = new HashMap<>();

    public void loadChapter(CompoundTag tag) {
        InputStream in = getAsset();
        //TODO

        try {
            in.close();
        } catch(Exception e) {
            // unlikely
        }
    }

    public InputStream getAsset() {
        return getClass().getResourceAsStream("/assets/" +
                ExactFeather.MOD_ID + "/books/" + name + ".txt");
    }

    public static ItemStack random(Player player) {
        int size = chapters.size();
        int rnd = player.getRandom().nextInt(size);
        Supplier<ItemStack>[] is = (Supplier<ItemStack>[]) chapters.values().toArray();
        return is[rnd].get();
    }
}
