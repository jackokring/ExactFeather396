package uk.co.kring.ef396.initializers;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.recipes.BrewingCommon;
import uk.co.kring.ef396.recipes.MobEffectCommon;
import uk.co.kring.ef396.recipes.PotionCommon;
import uk.co.kring.ef396.utilities.RegistryMap;

import java.util.function.Supplier;

public class BrewInit {

    public static final RegistryMap<Potion> POTIONS
            = new RegistryMap<Potion>(DeferredRegister.create(ForgeRegistries.POTIONS, ExactFeather.MOD_ID));

    // Potions
    public static final Triplet POTION_1 = new Triplet("p1",
            () -> new PotionCommon(new MobEffectCommon(MobEffects.WITHER)));

    // Recipes
    static {
        BrewingRecipeRegistry.addRecipe(new BrewingCommon(// TODO brew
                Ingredient.EMPTY,
                Ingredient.EMPTY,
                ItemStack.EMPTY
        ));
    }

    public static class Triplet {

        public RegistryObject<Potion> POTION;
        public RegistryObject<PotionItem> NORMAL_POTION;
        public RegistryObject<LingeringPotionItem> LINGERING_POTION;
        public RegistryObject<SplashPotionItem> SPLASH_POTION;

        public Triplet(String name, Supplier<Potion> potion) {

            POTION = POTIONS.register("p1",
                    potion);

            NORMAL_POTION = ItemInit.ITEMS.register(name , () -> new PotionItem(
                    new Item.Properties().tab(ExactFeather.TAB)
            ));
            LINGERING_POTION = ItemInit.ITEMS.register("lingering_" + name,
                    () -> new LingeringPotionItem(
                    new Item.Properties().tab(ExactFeather.TAB)
            ));
            SPLASH_POTION = ItemInit.ITEMS.register("splash_" + name,
                    () -> new SplashPotionItem(
                    new Item.Properties().tab(ExactFeather.TAB)
            ));
        }
    }
}
