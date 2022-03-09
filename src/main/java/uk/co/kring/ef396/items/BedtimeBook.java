package uk.co.kring.ef396.items;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.utilities.Registries;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.function.Supplier;

public class BedtimeBook extends WrittenBookItem implements IForgeRegistryEntry<Item> {

    //"item.ef396.book_1": "Book at Bedtime",
    private final String name;

    private static class Entry {
        Supplier<ItemStack> sup;
        ForgeConfigSpec.ConfigValue<String> altName;
        CompoundTag tag = new CompoundTag();
        String[] paras;
        ForgeConfigSpec.ConfigValue<String> serverDisclaimer;

        Entry(ForgeConfigSpec.ConfigValue<String> altName,
              ForgeConfigSpec.ConfigValue<String> serverDisclaimer) {;
            this.altName = altName;
            this.serverDisclaimer = serverDisclaimer;
            String loaded;
            try {
                loaded = new String(getClass().getResourceAsStream("/assets/" +
                    ExactFeather.MOD_ID + "/books/" + altName.get() + ".txt")
                    .readAllBytes(), StandardCharsets.UTF_8).trim();
            } catch(Exception e) {
                loaded = "Failed load of /assets/" + ExactFeather.MOD_ID + "/books/" + altName.get() + ".txt";
            }
            paras = loaded.split("\n\n");
            loadChapter();
            jsonify(paginate(paras));
        }

        String processPara(String para) {
            return null;//TODO substitutes
        }

        String[] paginate(String[] paras) {
            return null;//TODO fits on page
        }

        void jsonify(String[] pages) {
            //TODO tag like
            ListTag lt = new ListTag();
            tag.put("pages", lt);
            CompoundTag page = new CompoundTag();
            lt.add(page);
            page.putString("text", "The Book of Void");
            page.putString("color", "red");
        }

        void loadChapter() {
            // pages:[{"text":"The Book of Void","color":"red"}],title:Void,author:ef396,generation:3

            // placement of basics signed at bottom titled book
            if(paras.length < 1) return;
            tag.putString("name", paras[0]);
            tag.putString("title", paras[0]);
            tag.putString("author", paras[paras.length - 1]);
            tag.putInt("generation", 3);//it's an oldie but a goody
            if(paras.length < 3) return;
            for(int i = 0; i < paras.length - 2; i++) {
                paras[i] = processPara(paras[i + 1]);//leave last two empty?
            }
            //as title reproduction time waste removed
            //an injection of a disclaimer?
            paras[paras.length - 2] = serverDisclaimer.get();
        }

        void addSupplier(Supplier<ItemStack> sup) {
            this.sup = sup;
        }
    }

    private static HashMap<String, Entry> chapters = new HashMap<>();

    public static RegistryObject<Item> register(String name) {
        return Registries.items.register(name, () -> new BedtimeBook(
                new Item.Properties().stacksTo(1)
                        .tab(ExactFeather.TAB),
                name),//using name
                (builder) -> {
                    chapters.put(name, new Entry(builder.readString(name),
                            builder.readStringOpt(name + ".serverInjection",
                                    "And they all lived happily ever after.")));
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
