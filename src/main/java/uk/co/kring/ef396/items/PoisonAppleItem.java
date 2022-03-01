package uk.co.kring.ef396.items;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.IForgeRegistryEntry;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.utilities.Configurator;

public class PoisonAppleItem extends Item implements IForgeRegistryEntry<Item> {

    private static float probWither;
    private static float probPoison;
    private static float probHunger;

    public PoisonAppleItem() {
        super(new Item.Properties()
                .tab(ExactFeather.TAB)
                .food((new FoodProperties.Builder())
                        .nutrition(4)
                        .saturationMod(1.2f)
                        .effect(() -> new MobEffectInstance(MobEffects.WITHER, 300, 0),
                                probWither)
                        .effect(() -> new MobEffectInstance(MobEffects.POISON, 300, 1),
                                probPoison)
                        .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 300, 0),
                                probHunger)
                        .alwaysEat()
                        .build()
                ).stacksTo(33)
        );
    }

    public static void loadConfig(Configurator.Builder builder) {
        builder.comment("Control probability of effects");
        probWither = builder.readFloat("probWither", 0.1f, 0.f, 1.0f);
        probPoison = builder.readFloat("probPoison", 1.0f, 0.f, 1.0f);
        probHunger = builder.readFloat("probHunger", 0.3f, 0.f, 1.0f);
    }
}
