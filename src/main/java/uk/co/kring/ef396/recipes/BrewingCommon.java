package uk.co.kring.ef396.recipes;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.entities.initials.UnitaryAttribute;
import uk.co.kring.ef396.utilities.Registries;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BrewingCommon extends BrewingRecipe {

    public static Attribute A = new UnitaryAttribute("zz", 0);

    public BrewingCommon(Potion input, RegistryObject<? extends Item> ingredient,
                         RegistryObject<Potion> potion) {
        super(Ingredient.of(new ItemStack((ItemLike) input)),
                Ingredient.of(new ItemStack(ingredient.get())),
                getPotionStack(potion));
    }

    public BrewingCommon(Potion input, Item ingredient, RegistryObject<Potion> called) {
        super(Ingredient.of(new ItemStack((ItemLike) input)),
                Ingredient.of(new ItemStack(ingredient)),
                getPotionStack(called));
    }

    public static ItemStack getPotionStack(RegistryObject<Potion> called) {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), called.get());
    }

    public static final Item[] items = {
            /* The Gang of 13 */

            Items.SUGAR,                    //p
            Items.MAGMA_CREAM,              //T
            Items.RABBIT_FOOT,              //x
            Items.GLISTERING_MELON_SLICE,   //E
            Items.BLAZE_POWDER,             //P
            Items.GHAST_TEAR,               //t
            Items.SPIDER_EYE,               //e
            // Items.REDSTONE_DUST, -> leave as Mundane
            Items.GOLDEN_CARROT,            //Fire
            Items.TURTLE_HELMET,            //Earth
            Items.PUFFERFISH,               //Water
            Items.PHANTOM_MEMBRANE,         //Air
            // Items.FERMENTED_SPIDER_EYE, -> leave for corrupt, and usual
    };

    private static Attribute[] ua;

    public static Attribute[] attributes() {
        return ua;// unique static
    }

    public static Map<Item, RegistryObject<Potion>> mundaneFix() {
        // extra base alchemy-cals
        var reg = Registries.potions;
        var reg2 = Registries.attributes;
        HashMap<Item, RegistryObject<Potion>> list = new HashMap<>();
        ua = new Attribute[items.length];
        for(int i = 0; i < items.length; i++) {
            String name = items[i].getRegistryName().getPath();
            Supplier<Attribute> tmp = () -> new UnitaryAttribute(name,
                    0).setSyncable(true);
            var tmp3 = reg2.register(name, tmp);
            ua[i] = tmp3.get();//get unique
        }
        Arrays.stream(items).forEach((what) -> {
            list.put(what, reg.regAlchemyBase(what.getRegistryName().getPath(), what));
        });
        return Collections.unmodifiableMap(list);
    }
}
