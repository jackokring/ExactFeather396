package uk.co.kring.ef396;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import uk.co.kring.ef396.blocks.ModelledBlock;
import uk.co.kring.ef396.items.ModelledItem;
import uk.co.kring.ef396.utilities.Registries;
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
                protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer)  {

                }
            });
            /* gen.addProvider(new LootTableProvider(gen) {
                @Override
                protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
                    return super.getTables();
                }
            }); */
            var blockTags = new BlockTagsProvider(gen, ExactFeather.MOD_ID, file) {
                @Override
                protected void addTags() {
                    /* var reg = Registries.blocks;
                    reg.getEntries().forEach((block) -> {
                        Block b = block.get();
                        // more complex block types
                        if (b instanceof Block) {
                            /* BlockTags.MINEABLE_WITH_PICKAXE.
                                    .add(b);
                            tag(BlockTags.NEEDS_IRON_TOOL)
                                    .add(b);
                            tag(Tags.Blocks.ORES)
                                    .add(b);
                            return;

                        }
                    }); */
                }
            };
            gen.addProvider(blockTags);
            gen.addProvider(new ItemTagsProvider(gen, blockTags, ExactFeather.MOD_ID, file) {
                @Override
                protected void addTags() {
                    /* var reg = Registries.items;
                    reg.getEntries().forEach((item) -> {
                        Item b = item.get();
                        // more complex block types
                        if (b instanceof Item) {
                            //Tags.Blocks.class.
                            /* tag(Tags.Items.ORES)
                                    .add(b);
                            return;

                        }
                    }); */
                }
            });
        }
        if (event.includeClient()) {
            // ============= Excellent covers all cases likely =============
            gen.addProvider(new BlockStateProvider(gen, ExactFeather.MOD_ID, file) {
                @Override
                protected void registerStatesAndModels() {
                    Registries.blocks.getEntries().forEach((block) -> {
                        Block b = block.get();
                        if(b instanceof ModelledBlock) return;//needs own model ...
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
                        if(i instanceof ModelledItem) return;//needs own model ...
                        if(i instanceof ForgeSpawnEggItem) {
                            withExistingParent(i.getRegistryName().getPath(),
                                    mcLoc("item/template_spawn_egg"));
                            return;
                        }
                        if(i instanceof BlockItem) {
                            withExistingParent(i.getRegistryName().getPath(),
                                    modLoc("block/" + i.getRegistryName().getPath()));
                            return;//escape ...
                        }
                        singleTexture(i.getRegistryName().getPath(),
                                mcLoc("item/generated"),
                                "layer0", modLoc("item/" + i.getRegistryName().getPath()));
                    });
                }
            });
        }
    }
}
