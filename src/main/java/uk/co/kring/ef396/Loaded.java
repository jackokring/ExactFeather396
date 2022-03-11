package uk.co.kring.ef396;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.blocks.RubyBlock;
import uk.co.kring.ef396.blocks.RubyOre;
import uk.co.kring.ef396.entities.HogEntity;
import uk.co.kring.ef396.items.BedtimeBook;
import uk.co.kring.ef396.items.PoisonAppleItem;
import uk.co.kring.ef396.items.enums.ModArmorMaterial;
import uk.co.kring.ef396.items.enums.ModItemTier;
import uk.co.kring.ef396.recipes.BrewingCommon;
import uk.co.kring.ef396.recipes.MobEffectCommon;
import uk.co.kring.ef396.utilities.Configurator;
import uk.co.kring.ef396.utilities.Registries;
import uk.co.kring.ef396.utilities.RegistryMap;

import java.util.Map;
import java.util.function.Supplier;

public class Loaded {

    // for loading up things
    public Loaded() {
        ExactFeather.LOGGER.info("Initialization of statics in "
                + getClass().getCanonicalName() + " in progress.");
        init();//bootstrap
    }

    // Custom CreativeModeTab TAB
    public static final CreativeModeTab tab(Supplier<RegistryObject<Item>> icon) {
        return new CreativeModeTab(ExactFeather.MOD_ID) {
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(icon.get().get());
            }
            //that old Algol-68 triple ref pointer handles and an address to write
        };
    };

    public final void init() {
        Configurator.pushRegisterNest(Registries.items);
        items(Registries.items);
        Configurator.pushRegisterNest(Registries.potions);
        potions(Registries.potions);
        Configurator.pushRegisterNest(Registries.blocks);
        blocks(Registries.blocks);
        Configurator.pushRegisterNest(Registries.entities);
        entities(Registries.entities);
        Configurator.pushRegisterNest(Registries.sounds);
        sounds(Registries.sounds);
    }

    public static RegistryObject<Item> ruby, poison_apple, hogSpawnEgg;
    public static RegistryObject<Potion> psydare, psydareCorrupt;
    public static RegistryObject<Block> rubyBlock;
    public static Map<Item, RegistryObject<Potion>> mundane711;
    public static RegistryObject<EntityType<HogEntity>> hog;
    public static RegistryObject<SoundEvent> error, boom, wipple;
    public static CreativeModeTab tab;

    protected void items(RegistryMap<Item> reg) {
        tab = tab(() -> ruby);
        ruby = reg.register("ruby",
                () -> new Item(new Item.Properties().tab(tab)));
        poison_apple = reg.register("poison_apple", () -> new PoisonAppleItem(tab),
                (builder) -> PoisonAppleItem.loadConfig(builder));
        // Tools
        reg.register("ruby_sword",
                () -> new SwordItem(ModItemTier.RUBY, 2, -2.4F,
                        new Item.Properties().tab(tab)));
        reg.register("ruby_pickaxe",
                () -> new PickaxeItem(ModItemTier.RUBY, 0, -2.8F,
                        new Item.Properties().tab(tab)));
        reg.register("ruby_shovel",
                () -> new ShovelItem(ModItemTier.RUBY, 0.5F, -3.0F,
                        new Item.Properties().tab(tab)));
        reg.register("ruby_axe",
                () -> new AxeItem(ModItemTier.RUBY, 5, -3.1F,
                        new Item.Properties().tab(tab)));
        reg.register("ruby_hoe",
                () -> new HoeItem(ModItemTier.RUBY,-3, -1.0F,
                        new Item.Properties().tab(tab)));
        // Armor
        reg.register("ruby_helmet",
                () -> new ArmorItem(ModArmorMaterial.RUBY, EquipmentSlot.HEAD,
                        new Item.Properties().tab(tab)));
        reg.register("ruby_chestplate",
                () -> new ArmorItem(ModArmorMaterial.RUBY, EquipmentSlot.CHEST,
                        new Item.Properties().tab(tab)));
        reg.register("ruby_leggings",
                () -> new ArmorItem(ModArmorMaterial.RUBY, EquipmentSlot.LEGS,
                        new Item.Properties().tab(tab)));
        reg.register("ruby_boots",
                () -> new ArmorItem(ModArmorMaterial.RUBY, EquipmentSlot.FEET,
                        new Item.Properties().tab(tab)));
        // Books
        BedtimeBook.register("book_1", tab);
    }

    protected void potions(RegistryMap<Potion> reg) {
        MobEffectCommon me = new MobEffectCommon(MobEffects.CONFUSION);
        psydare = reg.registerPotionImmediate("psydare",
                poison_apple, me);//an active potion 1st step
        //an active potion made from primaries
        //should corrupt but that depends on an opposite
        //there is a problem with obtaining an inverse corruption
        //from a registered potion which has not been evaluated
        //and so the effect has the corruption handler and
        //adding must be manual
        psydareCorrupt = reg.register("psydare_corrupt",
                RegistryMap.registerPotionSecondary("psydare_corrupt",
                        psydare, Items.FERMENTED_SPIDER_EYE,
                        me.corrupt(true, false, false)));
        //uses registerAlchemyBase to make inactive base potions
        mundane711 = BrewingCommon.mundaneFix();
        // maybe an alchemy table?
    }

    protected void blocks(RegistryMap<Block> reg) {
        reg.regBlockItem(rubyBlock = reg.register("ruby_block", RubyBlock::new), tab);
        reg.regBlockItem(reg.register("ruby_ore", RubyOre::new), tab);
    }

    protected void entities(RegistryMap<EntityType<?>> reg) {
        hogSpawnEgg = reg.regEggItem(hog = reg.regMob("hog", HogEntity::new,
                HogEntity.getSizeXZ(), HogEntity.getSizeY()), tab);
                // this also does all renderer and attribute registration indirectly
    }

    protected void sounds(RegistryMap<SoundEvent> reg) {
        error = reg.regSound("error");
        boom = reg.regSound("boom");
        wipple = reg.regSound("wipple");
    }
}