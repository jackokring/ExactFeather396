package uk.co.kring.ef396.items;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.IForgeRegistryEntry;
import uk.co.kring.ef396.ExactFeather;

public class PoisonAppleItem extends Item implements IForgeRegistryEntry<Item> {

    public PoisonAppleItem() {
        super(new Item.Properties()
                .tab(ExactFeather.TAB)
                .food((new FoodProperties.Builder())
                        .nutrition(4)
                        .saturationMod(1.2f)
                        .effect(() -> new MobEffectInstance(MobEffects.WITHER, 300, 0), 1)
                        .effect(() -> new MobEffectInstance(MobEffects.POISON, 300, 1), 1)
                        .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 300, 0), 0.3f)
                        .alwaysEat()
                        .build())
        );
    }

    public static void loadConfig(ForgeConfigSpec.Builder builder) {

    }
}
