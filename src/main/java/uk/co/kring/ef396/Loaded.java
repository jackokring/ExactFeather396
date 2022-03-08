package uk.co.kring.ef396;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.blocks.RubyBlock;
import uk.co.kring.ef396.blocks.RubyOre;
import uk.co.kring.ef396.entities.HogEntity;
import uk.co.kring.ef396.items.BedtimeBook;
import uk.co.kring.ef396.items.PoisonAppleItem;
import uk.co.kring.ef396.items.enums.ModArmorMaterial;
import uk.co.kring.ef396.items.enums.ModItemTier;
import uk.co.kring.ef396.recipes.MobEffectCommon;
import uk.co.kring.ef396.utilities.Registries;
import uk.co.kring.ef396.utilities.RegistryMap;

public class Loaded {

    // for loading up things

    public static final void init(Loaded ref) {
        ref.items(Registries.items);
        ref.blocks(Registries.blocks);
        ref.potions(Registries.potions);
        ref.entities(Registries.entities);
        ref.sounds(Registries.sounds);
    }

    public static RegistryObject<? extends Item> ruby, poison_apple, hogSpawnEgg;
    public static RegistryObject<Block> rubyBlock;
    public static RegistryObject<Potion> psydare;
    public static RegistryObject<EntityType<? extends Mob>> hog;
    public static RegistryObject<SoundEvent> error;

    protected void items(RegistryMap<Item> reg) {
        ruby = reg.register("ruby",
                () -> new Item(new Item.Properties().tab(ExactFeather.TAB)));
        poison_apple = reg.register("poison_apple", PoisonAppleItem::new,
                (builder) -> PoisonAppleItem.loadConfig(builder));
        // Tools
        reg.register("ruby_sword",
                () -> new SwordItem(ModItemTier.RUBY, 2, -2.4F,
                        new Item.Properties().tab(ExactFeather.TAB)));
        reg.register("ruby_pickaxe",
                () -> new PickaxeItem(ModItemTier.RUBY, 0, -2.8F,
                        new Item.Properties().tab(ExactFeather.TAB)));
        reg.register("ruby_shovel",
                () -> new ShovelItem(ModItemTier.RUBY, 0.5F, -3.0F,
                        new Item.Properties().tab(ExactFeather.TAB)));
        reg.register("ruby_axe",
                () -> new AxeItem(ModItemTier.RUBY, 5, -3.1F,
                        new Item.Properties().tab(ExactFeather.TAB)));
        reg.register("ruby_hoe",
                () -> new HoeItem(ModItemTier.RUBY,-3, -1.0F,
                        new Item.Properties().tab(ExactFeather.TAB)));
        // Armor
        reg.register("ruby_helmet",
                () -> new ArmorItem(ModArmorMaterial.RUBY, EquipmentSlot.HEAD,
                        new Item.Properties().tab(ExactFeather.TAB)));
        reg.register("ruby_chestplate",
                () -> new ArmorItem(ModArmorMaterial.RUBY, EquipmentSlot.CHEST,
                        new Item.Properties().tab(ExactFeather.TAB)));
        reg.register("ruby_leggings",
                () -> new ArmorItem(ModArmorMaterial.RUBY, EquipmentSlot.LEGS,
                        new Item.Properties().tab(ExactFeather.TAB)));
        reg.register("ruby_boots",
                () -> new ArmorItem(ModArmorMaterial.RUBY, EquipmentSlot.FEET,
                        new Item.Properties().tab(ExactFeather.TAB)));
        // Books
        BedtimeBook.register("book_1");
    }

    protected void blocks(RegistryMap<Block> reg) {
        reg.regBlockItem(rubyBlock = reg.register("ruby_block", RubyBlock::new));
        reg.regBlockItem(reg.register("ruby_ore", RubyOre::new));
    }

    protected void potions(RegistryMap<Potion> reg) {
        psydare = reg.register("psydare",
                Potions.WATER, poison_apple, new MobEffectCommon(MobEffects.WITHER, 1));
    }

    protected void entities(RegistryMap<EntityType<?>> reg) {
        hogSpawnEgg = reg.regEggItem(hog = reg.register("hog",
                () -> EntityType.Builder.of(HogEntity::new, MobCategory.CREATURE)
                        .sized(HogEntity.getSizeXZ(), HogEntity.getSizeY()) // Hit box Size
                        .build(new ResourceLocation(ExactFeather.MOD_ID, "hog").toString())));
                // this also does all renderer and attribute registration indirectly
    }

    protected void sounds(RegistryMap<SoundEvent> reg) {
        error = reg.register("error",
                () -> new SoundEvent(new ResourceLocation(ExactFeather.MOD_ID, "error")));
    }
}
