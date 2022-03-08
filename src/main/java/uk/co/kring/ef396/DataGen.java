package uk.co.kring.ef396;

import com.mojang.datafixers.util.Pair;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import uk.co.kring.ef396.utilities.Registries;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = "ef396", bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGen {

    @SubscribeEvent
    public static void genData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var file = event.getExistingFileHelper();
        if (event.includeServer()) {
            gen.addProvider(new RecipeProvider(gen) {
                @Override
                protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer)  {

                }
            });
            gen.addProvider(new LootTableProvider(gen) {
                @Override
                protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
                    return super.getTables();
                }
            });
            var blockTags = new BlockTagsProvider(gen, ExactFeather.MOD_ID, file) {
                @Override
                protected void addTags() {
                    var reg = Registries.blocks;
                    reg.getEntries().forEach((block) -> {
                        Block b = block.get();
                        // more complex block types
                        if (b instanceof Block) {
                            /* BlockTags.MINEABLE_WITH_PICKAXE.
                                    .add(b);
                            tag(BlockTags.NEEDS_IRON_TOOL)
                                    .add(b);
                            tag(Tags.Blocks.ORES)
                                    .add(b); */
                            return;

                        }
                    });
                }
            };
            gen.addProvider(blockTags);
            gen.addProvider(new ItemTagsProvider(gen, blockTags, ExactFeather.MOD_ID, file) {
                @Override
                protected void addTags() {
                    var reg = Registries.items;
                    reg.getEntries().forEach((item) -> {
                        Item b = item.get();
                        // more complex block types
                        if (b instanceof Item) {
                            //Tags.Blocks.class.
                            /* tag(Tags.Items.ORES)
                                    .add(b); */
                            return;

                        }
                    });
                }
            });
        }
        if (event.includeClient()) {
            gen.addProvider(new BlockStateProvider(gen, ExactFeather.MOD_ID, file) {
                @Override
                protected void registerStatesAndModels() {
                    var reg = Registries.blocks;
                    reg.getEntries().forEach((block) -> {
                        Block b = block.get();
                        // more complex block types
                        if(b instanceof Block) {
                            simpleBlock(b);
                            return;
                        }
                    });
                }
            });
            gen.addProvider(new ItemModelProvider(gen, ExactFeather.MOD_ID, file) {
                @Override
                protected void registerModels() {
                    //Items TODO

                    var reg = Registries.blocks;
                    reg.getEntries().forEach((block) -> {
                        Block b = block.get();
                        String p = b.getRegistryName().getPath();
                        // more complex block types
                        if(b instanceof Block) {
                            withExistingParent(p,
                                    modLoc("block/" + p));//TODO??
                            return;
                        }
                    });
                }
            });
            gen.addProvider(new LanguageProvider(gen, ExactFeather.MOD_ID, "en_us") {
                @Override
                protected void addTranslations() {
                    add("itemGroup." + ExactFeather.MOD_ID, "ExactFeather");
                }
            });
        }
    }
}
