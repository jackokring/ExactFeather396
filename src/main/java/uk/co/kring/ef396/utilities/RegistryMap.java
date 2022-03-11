package uk.co.kring.ef396.utilities;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.HuskRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.entities.HogEntity;
import uk.co.kring.ef396.recipes.BrewingCommon;
import uk.co.kring.ef396.recipes.MobEffectCommon;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.*;

public final class RegistryMap<T extends IForgeRegistryEntry<T>> extends PriorityHashMap<String, Supplier<? extends T>> {

    private final DeferredRegister<T> register;
    private Collection<RegistryObject<T>> cache;

    public RegistryMap(DeferredRegister<T> register) {
        this.register = register;
    }

    public RegistryObject<Item> regBlockItem(RegistryObject<Block> block, CreativeModeTab tab) {
        return Registries.items.register(block.getId().getPath(),
                () -> new BlockItem(block.get(), new Item.Properties().tab(tab).stacksTo(64)));
    }

    public static int colors(RegistryObject<?> entity,
                      boolean highlight) {
        // gen color inspirations
        int h = entity.getId().toString().hashCode();//should be constant for mod and name
        int bg = (h & 0xffff);
        if(highlight) bg >>= 16;
        bg *= bg;
        bg &= 0xffffff;
        return bg;
    }

    public RegistryObject<T> onBotch(RegistryObject<T> object, String name, Supplier<T> replace) {
        if(object == null) {
            return register(name, replace);
        } else {
            return object;
        }
    }

    public RegistryObject<T> onBotch(RegistryObject<T> object, RegistryObject<T> replace) {
        if(object == null) {
            return replace;
        } else {
            return object;
        }
    }

    public static RegistryObject<EntityType<HogEntity>> regMob(String name,
                                                               EntityType.EntityFactory<HogEntity> entity,
                                                               float xz, float y) {
        EntityType.Builder builder = EntityType.Builder.of(
                        entity, MobCategory.CREATURE)
                .sized(xz, y); // Hit box Size
        return Registries.entities.register(name,
                () -> builder.build(new ResourceLocation(ExactFeather.MOD_ID, "hog").toString()));
    }

    public static RegistryObject<Item> regEggItem(
            RegistryObject<EntityType<HogEntity>> entity, CreativeModeTab tab
            /* int bg, int fg, String texture */) {
        ExactFeather.registerRender((event) -> {
            EntityRenderers.register(entity.get(),
                    (context) -> new HuskRenderer(context) {//default husk
                        @Override
                        public ResourceLocation getTextureLocation(Zombie fashion) {
                            String f = ((HogEntity)fashion).getFashion();
                            if(f == null) {
                                f = "";
                            } else {
                                f = "/" + f;
                            }
                            return new ResourceLocation(ExactFeather.MOD_ID,
                                    "textures/entity/" + entity.getId().getPath() + f + ".png");
                        }
                    });
        });
        ExactFeather.registerAttrib((event) -> {
            event.put(entity.get(), HogEntity.makeAttributes());
        });
        return Registries.items.register(entity.getId().getPath() + "_spawn_egg",
                () -> new ForgeSpawnEggItem(entity, colors(entity, false),
                        colors(entity, true),
                        new Item.Properties().tab(tab).stacksTo(64)));
    }

    public static RegistryObject<Potion> registerPotionImmediate(String name,
                                                                 RegistryObject<Item> add,
                                                                 MobEffectCommon... does) {
        RegistryObject<Potion> potion;
        if(does != null) {
            potion = Registries.potions.register(name,
                    () -> new Potion(does));
        } else {
            potion = Registries.potions.register(name,
                    () -> new Potion());
        }
        ExactFeather.registerRecipe((event) -> {
            BrewingRecipeRegistry.addRecipe(new BrewingCommon(
                    Potions.WATER, add, potion
            ));
        });
        return potion;
    }

    public static RegistryObject<Potion> registerPotionSecondary(String name, RegistryObject<Potion> in,
                                                  Item add,
                                                  MobEffectCommon... does) {
        RegistryObject<Potion> potion;
        if(does != null) {
             potion = Registries.potions.register(name,
                    () -> new Potion(does));
        } else {
            potion = Registries.potions.register(name,
                    () -> new Potion());
        }
        ExactFeather.registerRecipe((event) -> {
            BrewingRecipeRegistry.addRecipe(new BrewingCommon(
                    in.get(), add, potion
            ));
        });
        return potion;
    }

    public static RegistryObject<Potion> registerAlchemyBase(String name, Item item) {
        RegistryObject<Potion> potion = Registries.potions.register(name,
                    () -> new Potion());
        ExactFeather.registerRecipe((event) -> {
            BrewingRecipeRegistry.addRecipe(new BrewingCommon(
                    Potions.WATER, item, potion
            ));
        });
        return potion;
    }

    public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> sup,
                                                    Consumer<Configurator.Builder> user) {
        Configurator.configGame(name, user);
        return register(name, sup);
    }

    public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> sup) {
        return register.register(name, sup);
    }

    public void register(IEventBus bus) {
        register.register(bus);
    }

    public synchronized Collection<RegistryObject<T>> getEntries() {
        if(cache == null) {//prepare to use cache on first getEntries()
            //lazy defer
            cache = register.getEntries();
            cache.forEach(
                    registryObject -> {
                        String key = registryObject.getId().toString();
                        super.put(key, registryObject);
                    }
            );
            close();
        }
        return cache;
    }

    @Override
    public Supplier<? extends T> overwrite(String key, Supplier<? extends T> value) {
        exception();
        return null;
    }

    public Optional<T> getOptional(String key) {
        Supplier<? extends T> value = get(key);
        T recalled = null;
        if(value != null) recalled = value.get();
        return Optional.ofNullable(recalled);//easier to use one of these
    }

    @Override
    public int size() {
        getEntries();
        return super.size();
    }

    @Override
    public boolean isEmpty() {
        getEntries();
        return super.isEmpty();
    }

    @Override
    public Supplier<? extends T> get(Object key) {
        getEntries();
        return super.get(key);
    }

    @Override
    public Supplier<? extends T> put(String key, Supplier<? extends T> value) {
        exception();
        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        getEntries();
        return super.containsKey(key);
    }

    @Override
    public Supplier<? extends T> remove(Object key) {
        exception();
        return null;
    }

    @Override
    public void clear() {
        exception();
    }

    @Override
    public boolean containsValue(Object value) {
        getEntries();
        return super.containsValue(value);
    }

    @Override
    public Set<String> keySet() {
        getEntries();
        return super.keySet();
    }

    @Override
    public Collection<Supplier<? extends T>> values() {
        getEntries();
        return super.values();
    }

    @Override
    public Set<Entry<String, Supplier<? extends T>>> entrySet() {
        getEntries();
        return super.entrySet();
    }

    @Override
    public Supplier<? extends T> getOrDefault(Object key, Supplier<? extends T> defaultValue) {
        getEntries();
        return super.getOrDefault(key, defaultValue);
    }

    @Override
    public Supplier<? extends T> putIfAbsent(String key, Supplier<? extends T> value) {
        exception();
        return null;
    }

    @Override
    public boolean remove(Object key, Object value) {
        exception();
        return false;
    }

    @Override
    public boolean replace(String key, Supplier<? extends T> oldValue, Supplier<? extends T> newValue) {
        exception();
        return false;
    }

    @Override
    public Supplier<? extends T> replace(String key, Supplier<? extends T> value) {
        exception();
        return null;
    }

    @Override
    public Supplier<? extends T> computeIfAbsent(String key, Function<? super String, ? extends Supplier<? extends T>> mappingFunction) {
        exception();
        return null;
    }

    @Override
    public Supplier<? extends T> computeIfPresent(String key, BiFunction<? super String, ? super Supplier<? extends T>, ? extends Supplier<? extends T>> remappingFunction) {
        exception();
        return null;
    }

    @Override
    public Supplier<? extends T> compute(String key, BiFunction<? super String, ? super Supplier<? extends T>, ? extends Supplier<? extends T>> remappingFunction) {
        exception();
        return null;
    }

    @Override
    public Supplier<? extends T> merge(String key, Supplier<? extends T> value, BiFunction<? super Supplier<? extends T>, ? super Supplier<? extends T>, ? extends Supplier<? extends T>> remappingFunction) {
        exception();
        return null;
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super Supplier<? extends T>> action) {
        getEntries();
        super.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super Supplier<? extends T>, ? extends Supplier<? extends T>> function) {
        exception();
    }

    @Override
    public Object clone() {
        exception();
        return null;
    }

    private Exception exception() {
        throw new UnsupportedOperationException(this.getClass().toString() + ": is a RegistryMap!");
    }
}
