package uk.co.kring.ef396.initializers;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.recipes.BrewingCommon;
import uk.co.kring.ef396.recipes.MobEffectCommon;
import uk.co.kring.ef396.utilities.RegistryMap;

public class BrewInit {

    public static final RegistryMap<Potion> POTIONS
            = new RegistryMap<Potion>(DeferredRegister.create(ForgeRegistries.POTIONS, ExactFeather.MOD_ID));

    // Potions
    public static final RegistryObject<Potion> POTION_1 = POTIONS.register("p1",
            () -> new Potion(
                    new MobEffectCommon(MobEffects.WITHER, 100, 1)
            ));

    // Recipes
    public static void createBrewing() {
        BrewingRecipeRegistry.addRecipe(new BrewingCommon(// TODO brew
                Potions.WATER,   // In bottle
                ItemInit.POISON_APPLE.get(),   // In ingredient
                POTION_1
        ));
    }
}
