package uk.co.kring.ef396.items.enums;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeConfigSpec;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.initializers.ItemInit;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public enum ModArmorMaterial implements ArmorMaterial {

    RUBY("ruby", 25, new int[] { 2, 5, 6, 2 }, 27,
            SoundEvents.ARMOR_EQUIP_GENERIC, 0, () -> { return Ingredient.of(ItemInit.RUBY.get()); },0);

    private static ForgeConfigSpec.DoubleValue enchantScale;
    private static final int[] MAX_DAMAGE_ARRAY = new int[] { 11, 16, 15, 13 };
    private final String name;
    private final int maxDamageFactor; //Durability, Iron=15, Diamond=33, Gold=7, Leather=5
    private final int[] damageReductionAmountArray; //Armor Bar Protection, 1 = 1/2 armor bar
    private final int enchantability;
    private final SoundEvent soundEvent;
    private final float toughness; //Increases Protection, 0.0F=Iron/Gold/Leather, 2.0F=Diamond, 3.0F=Netherite
    private final Supplier<Ingredient> repairMaterial;
    private final float knockbackResistance; //1.0F=No Knockback, 0.0F=Disabled

    ModArmorMaterial(String name, int maxDamageFactor, int[] damageReductionAmountArray, int enchantability,
                     SoundEvent soundEvent, float toughness, Supplier<Ingredient> repairMaterial, float knockbackResistance) {
        this.name = ExactFeather.MOD_ID + name;
        this.maxDamageFactor = maxDamageFactor;
        this.damageReductionAmountArray = damageReductionAmountArray;
        this.enchantability = enchantability;
        this.soundEvent = soundEvent;
        this.toughness = toughness;
        this.repairMaterial = repairMaterial;
        this.knockbackResistance = knockbackResistance;
    }

    public static void setConfig(ForgeConfigSpec.Builder builder) {
        enchantScale = builder.defineInRange("enchantScale", 1.0f, 0.0f, 3.0f);
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlot slotIn) {
        return MAX_DAMAGE_ARRAY[slotIn.getIndex()] * this.maxDamageFactor;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot slotIn) {
        return this.damageReductionAmountArray[slotIn.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return (int)(this.enchantability * enchantScale.get());
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.soundEvent;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairMaterial.get();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
