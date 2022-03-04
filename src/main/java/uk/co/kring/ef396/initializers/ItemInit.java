package uk.co.kring.ef396.initializers;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.items.BedtimeBook;
import uk.co.kring.ef396.items.PoisonAppleItem;
import uk.co.kring.ef396.items.enums.ModArmorMaterial;
import uk.co.kring.ef396.items.enums.ModItemTier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import uk.co.kring.ef396.utilities.Configurator;
import uk.co.kring.ef396.utilities.RegistryMap;

public class ItemInit {

    public static final RegistryMap<Item> ITEMS
            = new RegistryMap<Item>(DeferredRegister.create(ForgeRegistries.ITEMS, ExactFeather.MOD_ID));

    static {
        // static tier configurators
        Configurator.configGame("armor", (builder) -> ModArmorMaterial.setConfig(builder));
        Configurator.configGame("tools", (builder) -> ModItemTier.setConfig(builder));
    }

    // Items
    public static final RegistryObject<Item> RUBY = ITEMS.register("ruby",
            () -> new Item(new Item.Properties().tab(ExactFeather.TAB)));

    public static final RegistryObject<PoisonAppleItem> POISON_APPLE
            = ITEMS.register("poison_apple", PoisonAppleItem::new,
                (builder) -> PoisonAppleItem.loadConfig(builder));

    public static final RegistryObject<ForgeSpawnEggItem> HOG_SPAWN_EGG
            = ITEMS.register("hog_spawn_egg",
            () -> new ForgeSpawnEggItem(EntityInit.HOG, 0x808080, 0x000000,
                    new Item.Properties().tab(ExactFeather.TAB).stacksTo(16)));

    public static final RegistryObject<Item> BOOK_1 = BedtimeBook.register("book_1");

    // Block Items
    public static final RegistryObject<Item> RUBY_BLOCK_ITEM = ITEMS.register("ruby_block",
            () -> new BlockItem(BlockInit.RUBY_BLOCK.get(), new Item.Properties().tab(ExactFeather.TAB)));

    public static final RegistryObject<Item> RUBY_ORE_ITEM = ITEMS.register("ruby_ore",
            () -> new BlockItem(BlockInit.RUBY_ORE.get(), new Item.Properties().tab(ExactFeather.TAB)));

    // Tools
    public static final RegistryObject<SwordItem> RUBY_SWORD = ITEMS.register("ruby_sword",
            () -> new SwordItem(ModItemTier.RUBY, 2, -2.4F,
                    new Item.Properties().tab(ExactFeather.TAB)));

    public static final RegistryObject<PickaxeItem> RUBY_PICKAXE = ITEMS.register("ruby_pickaxe",
            () -> new PickaxeItem(ModItemTier.RUBY, 0, -2.8F,
                    new Item.Properties().tab(ExactFeather.TAB)));

    public static final RegistryObject<ShovelItem> RUBY_SHOVEL = ITEMS.register("ruby_shovel",
            () -> new ShovelItem(ModItemTier.RUBY, 0.5F, -3.0F,
                    new Item.Properties().tab(ExactFeather.TAB)));

    public static final RegistryObject<AxeItem> RUBY_AXE = ITEMS.register("ruby_axe",
            () -> new AxeItem(ModItemTier.RUBY, 5, -3.1F,
                    new Item.Properties().tab(ExactFeather.TAB)));

    public static final RegistryObject<HoeItem> RUBY_HOE = ITEMS.register("ruby_hoe",
            () -> new HoeItem(ModItemTier.RUBY,-3, -1.0F,
                    new Item.Properties().tab(ExactFeather.TAB)));

    // Armor
    public static final RegistryObject<ArmorItem> RUBY_HELMET = ITEMS.register("ruby_helmet",
            () -> new ArmorItem(ModArmorMaterial.RUBY, EquipmentSlot.HEAD,
                    new Item.Properties().tab(ExactFeather.TAB)));

    public static final RegistryObject<ArmorItem> RUBY_CHESTPLATE = ITEMS.register("ruby_chestplate",
            () -> new ArmorItem(ModArmorMaterial.RUBY, EquipmentSlot.CHEST,
                    new Item.Properties().tab(ExactFeather.TAB)));

    public static final RegistryObject<ArmorItem> RUBY_LEGGINGS = ITEMS.register("ruby_leggings",
            () -> new ArmorItem(ModArmorMaterial.RUBY, EquipmentSlot.LEGS,
                    new Item.Properties().tab(ExactFeather.TAB)));

    public static final RegistryObject<ArmorItem> RUBY_BOOTS = ITEMS.register("ruby_boots",
            () -> new ArmorItem(ModArmorMaterial.RUBY, EquipmentSlot.FEET,
                    new Item.Properties().tab(ExactFeather.TAB)));
}
