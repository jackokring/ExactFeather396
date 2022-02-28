package uk.co.kring.ef396.initializers;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.recipes.BrewingCommon;
import uk.co.kring.ef396.utilities.RegistryMap;

public class BrewInit {

    public static final RegistryMap<Potion> POTIONS
            = new RegistryMap<Potion>(DeferredRegister.create(ForgeRegistries.POTIONS, ExactFeather.MOD_ID));

    // Potions
    public static final RegistryObject<Potion> POTION_1 = POTIONS.register("p1",
            () -> { return null; });//TODO potion

    // Recipes
    static {
        BrewingRecipeRegistry.addRecipe(new BrewingCommon(// TODO brew
                Ingredient.EMPTY,
                Ingredient.EMPTY,
                ItemStack.EMPTY
        ))
    }

}
