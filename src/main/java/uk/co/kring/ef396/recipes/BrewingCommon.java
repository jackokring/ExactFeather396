package uk.co.kring.ef396.recipes;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.registries.RegistryObject;

public class BrewingCommon extends BrewingRecipe {

    public BrewingCommon(Potion input, Item ingredient,
                         RegistryObject<Potion> potion) {
        super(Ingredient.of(new ItemStack((ItemLike) input)),
                Ingredient.of(new ItemStack(ingredient)),
                PotionUtils.setPotion(new ItemStack(Items.POTION), potion.get()));
    }

}
