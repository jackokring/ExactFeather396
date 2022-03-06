package uk.co.kring.ef396.recipes;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.initializers.BrewInit;

public class BrewingCommon extends BrewingRecipe {

    private BrewingCommon(Potion input, RegistryObject<Item> ingredient,
                         RegistryObject<Potion> potion) {
        super(Ingredient.of(new ItemStack((ItemLike) input)),
                Ingredient.of(new ItemStack(ingredient.get())),
                PotionUtils.setPotion(new ItemStack(Items.POTION), potion.get()));
    }

    public static RegistryObject<Potion> register(String name, Potion in, RegistryObject<Item> add,
                                                  MobEffectCommon does) {

        RegistryObject<Potion> potion = BrewInit.POTIONS.register(name,
                () -> new Potion(does));
        BrewingRecipeRegistry.addRecipe(new BrewingCommon(
                in, add, potion
        ));
        return potion;
    }
}
