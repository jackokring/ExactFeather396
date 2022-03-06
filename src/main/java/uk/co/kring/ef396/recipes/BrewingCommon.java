package uk.co.kring.ef396.recipes;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.initializers.BrewInit;
import uk.co.kring.ef396.initializers.ItemInit;

public class BrewingCommon extends BrewingRecipe {

    private BrewingCommon(Potion input, RegistryObject<? extends Item> ingredient,
                         RegistryObject<Potion> potion) {
        super(Ingredient.of(new ItemStack((ItemLike) input)),
                Ingredient.of(new ItemStack(ingredient.get())),
                PotionUtils.setPotion(new ItemStack(Items.POTION), potion.get()));
    }

    public static RegistryObject<Potion> register(String name, Potion in, RegistryObject<? extends Item> add,
                                                  MobEffectCommon does) {

        RegistryObject<Potion> potion = BrewInit.POTIONS.register(name,
                () -> new Potion(does));
        BrewingRecipeRegistry.addRecipe(new BrewingCommon(
                in, (RegistryObject<Item>) add, potion
        ));
        return potion;
    }

    public static class Brewing {
        public RegistryObject<Potion> register(String name, Potion in, RegistryObject<? extends Item> add,
                                               MobEffectCommon does) {
            return BrewingCommon.register(name, in, add, does);
        }
    }
}
