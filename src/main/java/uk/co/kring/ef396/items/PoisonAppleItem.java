package uk.co.kring.ef396.items;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.IForgeRegistryEntry;
import uk.co.kring.ef396.ExactFeather;

public class PoisonAppleItem extends Item implements IForgeRegistryEntry<Item> {

    private static ForgeConfigSpec.DoubleValue probWither;
    private static ForgeConfigSpec.DoubleValue probPoison;
    private static ForgeConfigSpec.DoubleValue probHunger;

    public PoisonAppleItem() {
        super(new Item.Properties()
                .tab(ExactFeather.TAB)
                .food((new FoodProperties.Builder())
                        .nutrition(4)
                        .saturationMod(1.2f)
                        .effect(() -> new MobEffectInstance(MobEffects.WITHER, 300, 0),
                                probWither.get().floatValue())
                        .effect(() -> new MobEffectInstance(MobEffects.POISON, 300, 1),
                                probPoison.get().floatValue())
                        .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 300, 0),
                                probHunger.get().floatValue())
                        .alwaysEat()
                        .build())
        );
    }

    public static void loadConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Control probability of effects");
        probWither = builder.defineInRange("probWither", 0.1f, 0.f, 1.0f);
        probPoison = builder.defineInRange("probPoison", 1.0f, 0.f, 1.0f);
        probHunger = builder.defineInRange("probHunger", 0.3f, 0.f, 1.0f);
    }
}
