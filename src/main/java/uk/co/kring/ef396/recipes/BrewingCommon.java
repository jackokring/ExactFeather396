package uk.co.kring.ef396.recipes;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.entities.initials.UnitaryAttribute;
import uk.co.kring.ef396.utilities.Configurator;
import uk.co.kring.ef396.utilities.Registries;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BrewingCommon extends BrewingRecipe {

    public static ForgeConfigSpec.IntValue baseID;

    public BrewingCommon(Potion input, RegistryObject<? extends Item> ingredient,
                         RegistryObject<Potion> potion) {
        super(Ingredient.of(getPotionStack(input)),
                Ingredient.of(new ItemStack(ingredient.get())),
                getPotionStack(potion));
    }

    public BrewingCommon(Potion input, Item ingredient, RegistryObject<Potion> called) {
        super(Ingredient.of(getPotionStack(input)),
                Ingredient.of(new ItemStack(ingredient)),
                getPotionStack(called));
    }

    public static ItemStack getPotionStack(RegistryObject<Potion> called) {
        return getPotionStack(called.get());
    }

    public static ItemStack getPotionStack(Potion called) {
        return PotionUtils.setPotion(new ItemStack(Items.POTION), called);
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

    public static final String uuid[] = {
            "429f864c-a7d0-496e-a13f-489804c26d99",              //p
            "8eb4c82e-50b9-4b94-86cb-e1a8c608aaf6",              //T
            "a328e07a-003f-45ce-849b-8cf3c0940545",              //x
            "aea80bad-bf75-4e0a-9eca-3e0fa64fd476",              //E
            "5dd09396-7d03-45ff-842a-e758915407b6",              //P
            "46169794-2ff2-4908-9fa5-a84c73a65340",              //t
            "6a4c3fbc-b90a-44be-921e-ccf0318cb95f",              //e

            "937b3d96-cd0c-4889-be31-34a3dad8a2bb",              //Fire
            "6ca663df-24fe-4f3a-8ad6-d34b5754ce55",              //Earth
            "bc2fcae2-b5b9-4c8b-b49c-c523f10e93a1",              //Water
            "3d1d43f4-9435-4474-9c40-d2a96b729545",              //Air
    };

    private static Attribute[] ua;
    private static MobEffect[] me;

    public static class MyEffect extends MobEffect {
        protected MyEffect(int attribute, boolean corrupt) {
            super(corrupt ? MobEffectCategory.HARMFUL : MobEffectCategory.BENEFICIAL,
                    baseID.get() + (attribute + 1) * (corrupt ? -1 : 1));
            addAttributeModifier(ua[attribute], uuid[attribute], corrupt ? -1 : 1,
                    AttributeModifier.Operation.ADDITION);
        }

        public int getColor() {
            return 0x00ffff;//cyan
        }
    }

    public static Attribute[] attributes() {
        return ua;// unique static
    }

    public static Map<Item, RegistryObject<Potion>> mundaneFix() {
        // setting id
        Configurator.configGame("potion.id.base", (builder) -> {
            baseID = builder.readInt("id", 396, 396, 9000);
        });
        // extra base alchemy-cals
        var reg = Registries.potions;
        RegistryObject<Potion> p;
        var reg2 = Registries.attributes;
        HashMap<Item, RegistryObject<Potion>> list = new HashMap<>();
        ua = new Attribute[items.length];
        for(int i = 0; i < items.length; i++) {
            String name = items[i].getRegistryName().getPath();
            Supplier<Attribute> tmp = () -> new UnitaryAttribute(name,
                    0).setSyncable(true);
            var tmp3 = reg2.register(name, tmp);
            ua[i] = tmp3.get();//get unique
            MobEffect me = new MyEffect(i, false);
            list.put(items[i], p = reg.regAlchemyBase(name, items[i], new MobEffectCommon(me)));
            MobEffect me2 = new MyEffect(i, true);
            reg.regAlchemyCorrupt(name + "_corrupt", p, new MobEffectCommon(me));
        }
        return Collections.unmodifiableMap(list);
    }
}
