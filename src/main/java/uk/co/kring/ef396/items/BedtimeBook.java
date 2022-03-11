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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;

public class BedtimeBook extends WrittenBookItem implements IForgeRegistryEntry<Item> {

    //"item.ef396.book_1": "Book at Bedtime",
    private final String name;

    private static class Entry {
        Supplier<ItemStack> sup;
        CreativeModeTab tab;
        ForgeConfigSpec.ConfigValue<String> altName;
        CompoundTag tag = new CompoundTag();
        String[] paras;
        ForgeConfigSpec.ConfigValue<String> serverDisclaimer;

        Entry(ForgeConfigSpec.ConfigValue<String> altName,
              ForgeConfigSpec.ConfigValue<String> serverDisclaimer,
              CreativeModeTab tab) {;
            this.altName = altName;
            this.tab = tab;
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
            jsonify(paginate(paras), tag);
        }

        String processPara(String para) {
            return para;//TODO substitutes
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

    //char widths and an extra 1 pix space between
    //so 14 lines, 57 pixels, 50 pages maximals
    private final static byte[] sizes = {
            0, 0, 0, 0,     0, 0, 0, 0,//CTRL
            0, 0, 0, 0,     0, 0, 0, 0,
            0, 0, 0, 0,     0, 0, 0, 0,
            0, 0, 0, 0,     0, 0, 0, 0,

            3, 1, 3, 5,     5, 5, 5, 1,//SYM
            3, 3, 3, 5,     1, 5, 1, 5,
            5, 5, 5, 5,     5, 5, 5, 5,
            5, 5, 1, 1,     4, 5, 4, 5,

            6, 5, 5, 5,     5, 5, 5, 5,//UPPER (64)
            5, 3, 5, 5,     5, 5, 5, 5,//72
            5, 5, 5, 5,     5, 5, 5, 5,//80
            5, 5, 5, 3,     5, 3, 5, 5,//88

            2, 5, 5, 5,     5, 5, 4, 5,//LOWER (96)
            5, 1, 5, 4,     2, 5, 5, 5,//104
            5, 5, 5, 5,     3, 5, 5, 5,//112
            5, 5, 5, 3,     1, 3, 6, 5 //120
    };

    //a space is 3 + 1 = 4 wide
    private static int sizeWord(String word) {
        int count = 0;
        for(int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if(c > 127) {
                count += 6;//include extra
            } else {
                count += sizes[c] + 1;
            }
        }
        if(count == 0) count = 1;//fixes next line
        return count - 1;//last character extra not extra
    }

    public static String[] paginate(String[] paras) {
        //page size
        ArrayList<String> pages = new ArrayList<>();
        String current = "";//to output
        int lineTotal = 0;
        int pageHeight = 0;
        for(int pa = 0; pa < paras.length; pa ++) {
            String parse = paras[pa];//get paragraph
            String[] words = parse.split(" ");
            int[] lens = new int[words.length];
            for(int c = 0; c < words.length; c++) {
                lens[c] = sizeWord(words[c]);
            }
            int c = 0;//current count index
            boolean exit = false;
            while(c < words.length) {
                while (pageHeight < 14) {//only 13 lines
                    while (lineTotal < 57) {//57 px
                        if (lineTotal + 4 + lens[c] < 57) {//with space
                            lineTotal += lens[c] + 4;
                            current += words[c] + " ";
                            c++;
                            if(c >= words.length) {
                                // ran out of text
                                exit = true;
                                break;
                            }
                        }
                    }
                    lineTotal = 0;
                    if(exit == true) {
                        if(pageHeight >= 11) {
                            pageHeight = -1;//new page
                        } else {
                            pageHeight += 2;
                            current += "\n\n";//new page paragraph
                        }
                        break;
                    }
                    pageHeight++;
                }
                exit = false;
                if(pageHeight == -1 || c >= words.length) {//needs new page
                    pages.add(current);
                    current = "";//new page
                    pageHeight = 0;//fix up detected exit without blanks
                }
            }//loop next paragraph
        }
        return (String[])pages.stream().toArray();
    }

    public static void jsonify(String[] pages, CompoundTag tag) {
        //TODO tag like
        ListTag lt = new ListTag();
        tag.put("pages", lt);
        for(int i = 0; i < pages.length; i++) {
            CompoundTag page = new CompoundTag();
            lt.add(page);
            page.putString("text", pages[i]);//all black
            //page.putString("color", "red");
        }
    }

    private static HashMap<String, Entry> chapters = new HashMap<>();

    public static RegistryObject<Item> register(String name, CreativeModeTab tab) {
        // delayed register with config settings
        return Registries.items.register(name, () -> new BedtimeBook(
                new Item.Properties().stacksTo(1)
                        .tab(tab),
                name),//using name
                (builder) -> {
                    //make an entry with config info
                    chapters.put(name, new Entry(builder.readString(name),
                            builder.readStringOpt(name + ".serverInjection",
                                    "And they all lived happily ever after."), tab));
                });// this name should be different for file by config
    }

    private BedtimeBook(Properties properties, String name) {
        // when instanced by registry make a new supplier of the item stacks
        super(properties);
        this.name = name;
        chapters.get(name).addSupplier(() -> new ItemStack(this, 1,
                chapters.get(name).tag));
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> stacks) {
        if(tab != null && !tab.equals(chapters.get(name).tab)) return;
        stacks.add(chapters.get(name).sup.get());
        //super.fillItemCategory(tab, stacks);
    }

    public static Optional<ItemStack> random(Player player) {
        int size = chapters.size();
        int rnd = player.getRandom().nextInt(size);
        Supplier<ItemStack>[] is
                = (Supplier<ItemStack>[]) chapters.values().stream().map((entry) -> entry.sup).toArray();
        if(is.length == 0) return Optional.empty();
        ItemStack i = is[rnd].get();
        return Optional.of(i);//allows for no values and sleep event no fail
    }
}
