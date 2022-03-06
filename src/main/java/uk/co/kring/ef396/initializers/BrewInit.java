package uk.co.kring.ef396.initializers;

import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
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

    // Recipes
    public static final RegistryObject<Potion> POISON_1 = BrewingCommon.register("p1",
        Potions.WATER, ItemInit.POISON_APPLE, new MobEffectCommon(MobEffects.WITHER, 1));
}
