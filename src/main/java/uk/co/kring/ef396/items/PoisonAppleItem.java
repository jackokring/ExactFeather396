package uk.co.kring.ef396.items;

import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.IForgeRegistryEntry;
import uk.co.kring.ef396.ExactFeather;

public class PoisonAppleItem extends Item implements IForgeRegistryEntry<PoisonAppleItem> {

    public PoisonAppleItem() {
        super(new Item.Properties()
                .tab(ExactFeather.TAB)
                .food(new Food.builder()
                        .hunger(4)
                        .saturation(1.2f)
                        .effect(new EffectInstance(Effects.NAUSEA, 300, 0), 1)
                        .effect(new EffectInstance(Effects.POISON, 300, 1), 1)
                        .effect(new EffectInstance(Effects.HUNGER, 300, 0), 0.3f)
                        .setAlwaysEdible()
                        .build())
        );
    }
}
