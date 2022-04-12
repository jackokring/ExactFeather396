package uk.co.kring.ef396;

import com.google.gson.JsonElement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.jetbrains.annotations.NotNull;
import uk.co.kring.ef396.blocks.ModelledBlock;
import uk.co.kring.ef396.items.ModelledItem;
import uk.co.kring.ef396.tags.*;
import uk.co.kring.ef396.utilities.JsonAdapter;
import uk.co.kring.ef396.data.Perl;
import uk.co.kring.ef396.utilities.Registries;

import java.util.*;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = "ef396", bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGen {

    @SubscribeEvent
    public static void genData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var file = event.getExistingFileHelper();
        if (event.includeServer()) {
            gen.addProvider(new RecipeProvider(gen) {
                @Override
                protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer)  {

                }
            });
            var blockTags = new BlockTagsProvider(gen, ExactFeather.MOD_ID, file) {
                @Override
                protected void addTags() {
                    var reg = Registries.blocks;
                    reg.getEntries().forEach((block) -> {
                        Block b = block.get();
                        if(b instanceof MinablePickaxe) tag(BlockTags.MINEABLE_WITH_PICKAXE).add(b);
                        if(b instanceof MinableStone) tag(BlockTags.NEEDS_STONE_TOOL).add(b);
                        if(b instanceof OreTag) tag(Tags.Blocks.ORES).add(b);
                    });
                }
            };
            gen.addProvider(blockTags);
            gen.addProvider(new ItemTagsProvider(gen, blockTags, ExactFeather.MOD_ID, file) {
                @Override
                protected void addTags() {
                    var reg = Registries.items;
                    reg.getEntries().forEach((item) -> {
                        Item i = item.get();
                        if (i instanceof BlockItem bi) {
                            Block b = bi.getBlock();
                            if(b instanceof OreTag) tag(Tags.Items.ORES).add(i);
                        }
                        if(i instanceof GemTag) tag(Tags.Items.GEMS).add(i);
                        if(i instanceof PiglinLovedTag) tag(ItemTags.PIGLIN_LOVED).add(i);
                    });
                }
            });
        }
        if (event.includeClient()) {
            // ======================== MACRO EXPANDER ====================
            gen.addProvider(new LanguageProvider(gen, ExactFeather.MOD_ID, "en_us") {
                @FunctionalInterface
                public interface FunkyHashEntry {
                    void processEntry(String key, String entry);
                }

                public void iterate(FunkyHashEntry hashEntry) {
                    JsonAdapter ja = new JsonAdapter(file, "lang",
                            "en_us", PackType.CLIENT_RESOURCES);
                    ja.forEach((entry) -> {
                        String key = entry.getKey();
                        JsonElement val = entry.getValue();
                        String v = val.getAsString();
                        hashEntry.processEntry(key, v);
                    });
                }

                static class Entry {//key is post $$ expression
                    final String prefix;
                    final String postfix;
                    final String replaced;

                    public Entry(String key, String val) {
                        replaced = val;//substitution
                        String trimmed = trimmedKey(key);
                        int idx = key.indexOf(trimmed);
                        prefix = (idx > 2) ? key.substring(0, idx - 3) : "";// for ".$$"
                        postfix = (idx + trimmed.length() < key.length())
                                ? key.substring(idx + trimmed.length() + 1)
                                : "";// "."
                    }

                    public static boolean isKey(String key) {
                        return key.contains("$$");
                    }

                    public static String trimmedKey(String key) {
                        return key.split(new Perl("$$").get())[1]
                                .split(new Perl(".").get())[0];
                    }

                    public static boolean valueHasKey(String val) {
                        return val.contains("$") && !val.contains("%");// formatted? avoid
                    }

                    public static String expandValue(String key, String val) {
                        String error = key;
                        while (valueHasKey(val)) {
                            String k = maximalKey(val);
                            while(k.length() > 0) {
                                List<Entry> el = hashMap.get(k);
                                if(el != null) {
                                    for (Entry e : el) {
                                        if (key.contains(e.postfix)) {// for matching substitution
                                            int at = val.indexOf("$");
                                            val = val.substring(0, at)
                                                    + e.replaced
                                                    + val.substring(at + k.length() + 1);// keyed !!
                                            break;// first only
                                        }
                                    }
                                }
                                k = k.substring(0, k.length() - 1);//reduce key
                                if(k.length() == 0) {
                                    int at = val.indexOf("$");
                                    val = val.substring(0, at) + "\"" + maximalKey(val)
                                            + "\"" + val.substring(at + 1);
                                    ExactFeather.LOGGER.error("\"" + error + "\" has missing $$key.");
                                    return val;
                                }
                            }
                        }
                        return val;
                    }

                    public static String maximalKey(String any) {
                        return any.split(new Perl("$").get())[1]
                                .split(new Perl(".").get())[0];
                    }
                }

                public static final HashMap<String, List<Entry>> hashMap = new HashMap<>();

                private void the(String key, String val) {//pass 2
                    // substitutions
                    String error = key;
                    if (key.contains("$")) {
                        String k = Entry.maximalKey(key);
                        while (k.length() > 0) {
                            List<Entry> el = hashMap.get(k);
                            if (el != null) {
                                for (Entry e : el) {
                                    if (key.contains(e.prefix)) {
                                        int at = key.indexOf("$");
                                        String nestKey = key.substring(0, at)
                                                + e.postfix
                                                + key.substring(at + k.length() + 1);// keyed !!
                                        // so a lot of intermediate expansions happen
                                        String nestVal = Entry.expandValue(nestKey, val);
                                        add(nestKey, nestVal);
                                        the(nestKey, nestVal);
                                    }
                                }
                            }
                            k = k.substring(0, k.length() - 1);//reduce key
                            if (k.length() == 0) {
                                ExactFeather.LOGGER.error("\"" + error + "\" has missing $$key.");
                                int at = key.indexOf("$");
                                key = key.substring(0, at) + key.substring(at + 1);//drop $
                            }
                        }
                    }
                }

                @Override
                protected void addTranslations() {
                    iterate((key, val) -> {//pass 1
                        if(Entry.isKey(key)) {
                            String trimmed = Entry.trimmedKey(key);
                            List<Entry> le = hashMap.computeIfAbsent(trimmed,
                                    k -> new LinkedList<>());
                            le.add(new Entry(key, val));//append
                        }
                    });
                    iterate(this::the);//pass 2
                }
            });
            // ============= Excellent covers all cases likely =============
            gen.addProvider(new BlockStateProvider(gen, ExactFeather.MOD_ID, file) {
                @Override
                protected void registerStatesAndModels() {
                    Registries.blocks.getEntries().forEach((block) -> {
                        Block b = block.get();
                        if(b instanceof ModelledBlock bm) {
                            bm.provideModel(this, b);
                            return;//needs own model ...
                        }
                        simpleBlock(b);
                    });
                }
            });
            // ============= Excellent covers all cases likely =============
            gen.addProvider(new ItemModelProvider(gen, ExactFeather.MOD_ID, file) {
                @Override
                protected void registerModels() {
                    Registries.items.getEntries().forEach((item) -> {
                        Item i = item.get();
                        if(i instanceof ModelledItem im) {
                            im.provideModel(this, i);
                            return;//needs own model ...
                        }
                        if(i instanceof ForgeSpawnEggItem) {
                            withExistingParent(Objects.requireNonNull(i.getRegistryName()).getPath(),
                                    mcLoc("item/template_spawn_egg"));
                            return;
                        }
                        if(i instanceof BlockItem) {
                            withExistingParent(Objects.requireNonNull(i.getRegistryName()).getPath(),
                                    modLoc("block/" + i.getRegistryName().getPath()));
                            return;//escape ...
                        }
                        singleTexture(Objects.requireNonNull(i.getRegistryName()).getPath(),
                                mcLoc("item/generated"),
                                "layer0", modLoc("item/" + i.getRegistryName().getPath()));
                    });
                }
            });
        }
    }
}
