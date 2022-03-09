package uk.co.kring.ef396.items;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.IForgeRegistryEntry;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.utilities.Configurator;

public class PoisonAppleItem extends Item implements IForgeRegistryEntry<Item> {

    private static ForgeConfigSpec.DoubleValue probWither;
    private static ForgeConfigSpec.DoubleValue probPoison;
    private static ForgeConfigSpec.DoubleValue probHunger;
    private static ForgeConfigSpec.IntValue time;

    public PoisonAppleItem() {
        super(new Item.Properties()
                .tab(ExactFeather.TAB)
                .food((new FoodProperties.Builder())
                        .nutrition(4)
                        .saturationMod(1.2f)
                        .effect(() -> new MobEffectInstance(MobEffects.CONFUSION, time.get(), 0),
                                probWither.get().floatValue())
                        .effect(() -> new MobEffectInstance(MobEffects.POISON, time.get(), 1),
                                probPoison.get().floatValue())
                        .effect(() -> new MobEffectInstance(MobEffects.HUNGER, time.get(), 0),
                                probHunger.get().floatValue())
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
        time = builder.readInt("time", 100, 25, 300);
    }
}
