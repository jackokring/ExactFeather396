package uk.co.kring.ef396.items.enums;

import net.minecraft.world.item.crafting.Ingredient;;
import net.minecraft.world.item.Tier;
import net.minecraftforge.common.ForgeConfigSpec;
import uk.co.kring.ef396.Loaded;
import uk.co.kring.ef396.utilities.Configurator;

import java.util.function.Supplier;

public enum ModItemTier implements Tier {

    RUBY(3, 800, 7.0F, 3.0F, 18, () -> {
        return Ingredient.of(Loaded.ruby.get());
    });

    static {
        // static tier configurators
        Configurator.configGame("tools", (builder) -> setConfig(builder));
    }

    private static ForgeConfigSpec.DoubleValue enchantScale;
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

    public static void setConfig(Configurator.Builder builder) {
        enchantScale = builder.readFloat("enchantScale", 1.0f, 0.0f, 3.0f);
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
        return (int)(this.enchantability * enchantScale.get());
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairMaterial.get();
    }
}
