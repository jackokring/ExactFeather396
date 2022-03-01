package uk.co.kring.ef396.items.enums;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ForgeConfigSpec;
import uk.co.kring.ef396.initializers.ItemInit;
import net.minecraft.world.item.Tier;

import java.util.function.Supplier;

public enum ModItemTier implements Tier {

    RUBY(3, 800, 7.0F, 3.0F, 18, () -> {
        return Ingredient.of(ItemInit.RUBY.get());
    });

    private static float enchantScale;
    private final int harvestLevel;
    private final int maxUses;
    private final float efficiency;
    private final float attackDamage;
    private final int enchantability;
    private final Supplier<Ingredient> repairMaterial;

    ModItemTier(int harvestLevel, int maxUses, float efficiency, float attackDamage, int enchantability, Supplier<Ingredient> repairMaterial) {
        this.harvestLevel = harvestLevel;
        this.maxUses = maxUses;
        this.efficiency = efficiency;
        this.attackDamage = attackDamage;
        this.enchantability = enchantability;
        this.repairMaterial = repairMaterial;
    }

    public static void setConfig(ForgeConfigSpec.Builder builder) {
        enchantScale = builder.defineInRange("enchantScale", 1.0f, 0.0f, 3.0f)
                .get().floatValue();
    }

    @Override
    public int getUses() {
        return maxUses;
    }

    @Override
    public float getSpeed() {
        return efficiency;
    }

    @Override
    public float getAttackDamageBonus() {
        return attackDamage;
    }

    @Override
    public int getLevel() {
        return harvestLevel;
    }

    @Override
    public int getEnchantmentValue() {
        return (int)(this.enchantability * enchantScale);
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairMaterial.get();
    }
}
