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
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.utilities.Registries;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BrewingCommon extends BrewingRecipe {

    public BrewingCommon(Potion input, RegistryObject<? extends Item> ingredient,
                         RegistryObject<Potion> potion) {
        super(Ingredient.of(new ItemStack((ItemLike) input)),
                Ingredient.of(new ItemStack(ingredient.get())),
                PotionUtils.setPotion(new ItemStack(Items.POTION), potion.get()));
    }

    public BrewingCommon(Item ingredient, RegistryObject<Potion> called) {
        super(Ingredient.of(new ItemStack((ItemLike) Potions.WATER)),
                Ingredient.of(new ItemStack(ingredient)),
                PotionUtils.setPotion(new ItemStack(Items.POTION), called.get()));
    }

    public static Map<Item, RegistryObject<Potion>> mundaneFix() {
        // extra base alchemy-cals
        var reg = Registries.potions;
        HashMap<Item, RegistryObject<Potion>> list = new HashMap<>();
        Item[] items = {
            Items.SUGAR,
            Items.MAGMA_CREAM,
            Items.RABBIT_FOOT,
            Items.GLISTERING_MELON_SLICE,
            Items.BLAZE_POWDER,
            Items.GHAST_TEAR,
            Items.SPIDER_EYE
                //Items.REDSTONE_DUST -> leave as Mundane
        };
        Arrays.stream(items).forEach((what) -> {
            list.put(what, reg.register(what.getRegistryName().getPath(), what));
        });
        return Collections.unmodifiableMap(list);
    }
}
