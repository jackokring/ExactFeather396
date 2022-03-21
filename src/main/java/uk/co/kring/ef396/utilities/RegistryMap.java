package uk.co.kring.ef396.utilities;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;
import org.jetbrains.annotations.NotNull;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.blocks.EnergyBlock;
import uk.co.kring.ef396.blocks.entities.EnergyContainer;
import uk.co.kring.ef396.blocks.entities.EnergyEntity;
import uk.co.kring.ef396.blocks.entities.EnergyScreen;
import uk.co.kring.ef396.entities.HogEntity;
import uk.co.kring.ef396.entities.initials.HogInitials;
import uk.co.kring.ef396.items.BedtimeBook;
import uk.co.kring.ef396.recipes.BrewingCommon;
import uk.co.kring.ef396.recipes.MobEffectCommon;

import java.util.*;
import java.util.function.*;

public final class RegistryMap<T extends IForgeRegistryEntry<T>> extends PriorityHashMap<String, Supplier<? extends T>> {

    private final DeferredRegister<T> register;
    private Collection<RegistryObject<T>> cache;

    public RegistryMap(DeferredRegister<T> register) {
        this.register = register;
    }

    public RegistryObject<Item> regBlockItem(RegistryObject<Block> block, CreativeModeTab tab) {
        printClassWrong(Registries.blocks, block.getId().getPath());
        return Registries.items.register(block.getId().getPath(),
                () -> new BlockItem(block.get(), new Item.Properties().tab(tab).stacksTo(64)));
    }

    public RegistryObject<Item> regBlockItem(RegistryBlockGroup group, CreativeModeTab tab) {
        RegistryObject<EnergyBlock> block = group.get();
        return Registries.items.register(block.getId().getPath(),
                () -> new BlockItem(block.get(), new Item.Properties().tab(tab).stacksTo(64)));
    }

    public RegistryObject<Item> regBook(String name, CreativeModeTab tab) {
        printClassWrong(Registries.items, name);
        return BedtimeBook.register(name, tab);
    }

    public synchronized RegistryBlockGroup regEnergyBlock(String name, Supplier<EnergyBlock> blockSupplier,
                                                BlockEntityType.BlockEntitySupplier<EnergyEntity>
                                                        blockEntitySupplier,
                                                // allow following 2 to be null
                                                Funky container,
                                                MenuScreens.ScreenConstructor<EnergyContainer,
                                                        EnergyScreen> screen) {
        printClassWrong(Registries.blocks, name);

        var lastEntity = Registries.blockEntities.register(name,
                () -> BlockEntityType.Builder.of(blockEntitySupplier,
                            blockSupplier.get()).build(null)
                );
        RegistryObject<MenuType<EnergyContainer>> menuEasy = null;
        if(container != null) {
            menuEasy = Registries.containers.register(name,
                    () -> Obtain.menuTypeFrom(container));
            if(screen != null) {
                // on client setup so renderer
                RegistryObject<MenuType<EnergyContainer>> finalMenuEasy = menuEasy;
                ExactFeather.registerRender((event) -> {
                    MenuScreens.register(finalMenuEasy.get(), screen);
                    // Attach our container to the screen
                });
            }
        }
        return new RegistryBlockGroup(Registries.blocks.register(name, blockSupplier),
                lastEntity, menuEasy, blockEntitySupplier, container);
    }

    public int colors(RegistryObject<?> entity,
                      boolean highlight) {
        // gen color inspirations
        int h = entity.getId().toString().hashCode();//should be constant for mod and name
        int bg = (h & 0xffff);
        if(highlight) bg >>= 16;
        bg *= bg;
        bg &= 0xffffff;
        return bg;
    }

    public RegistryObject<? extends T> onBotch(RegistryObject<? extends T> object,
                                               String name, Supplier<? extends T> replace) {
        if(object == null) {
            return register(name, replace);
        } else {
            return object;
        }
    }

    public RegistryObject<? extends T> onBotch(RegistryObject<? extends T> object,
                                               RegistryObject<? extends T> replace) {
        if(object == null) {
            return replace;
        } else {
            return object;
        }
    }

    public static class HogModel extends HumanoidModel<HogEntity> {
        // default simple humanoid renderer
        public static final String BODY = "body";

        public static ModelLayerLocation HOG_LAYER
                = new ModelLayerLocation(
                        new ResourceLocation(ExactFeather.MOD_ID, "hog"), BODY);

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = createMesh(CubeDeformation.NONE, 0.6f);
            return LayerDefinition.create(meshdefinition, 64, 32);
        }

        public HogModel(ModelPart part) {
            super(part);
        }
    }

    public RegistryObject<EntityType<HogEntity>> regMob(String name,
                                                        EntityType.EntityFactory<HogEntity> entity,
                                                        Supplier<HogInitials> onNew) {
        printClassWrong(Registries.entities, name);
        // entity builder
        HogInitials hi = onNew.get();
        EntityType.Builder<HogEntity> builder = EntityType.Builder.of(
                        entity, MobCategory.CREATURE)
                .sized(0.6f, 1.95f) // Hit box Size
                .clientTrackingRange(8)
                .setShouldReceiveVelocityUpdates(false);
        // entity supplier
        Supplier<EntityType<HogEntity>> he =
                () -> builder.build(new ResourceLocation(ExactFeather.MOD_ID, "hog").toString());
        // model control of layers
        ExactFeather.registerLayers((event) -> {
            event.registerLayerDefinition(HogModel.HOG_LAYER, HogModel::createBodyLayer);
        });
        // renderer for skin
        ExactFeather.registerRender((event) -> EntityRenderers.register(he.get(),
                (context) -> new HumanoidMobRenderer<HogEntity, HogModel>(context,
                        new HogModel(context.bakeLayer(HogModel.HOG_LAYER)), 1f) {
                    @Override
                    @NotNull
                    public ResourceLocation getTextureLocation(@NotNull HogEntity fashion) {
                        String f = fashion.getFashion();
                        if(f == null) {
                            f = "";
                        } else {
                            f = "/" + f;
                        }
                        return new ResourceLocation(ExactFeather.MOD_ID,
                                "textures/entity/" + name + f + ".png");
                    }
                }));
        // actionable attributes registration
        ExactFeather.registerAttrib((event)
                -> event.put(he.get(), HogEntity.createLivingAttributes().build()));
        // biome specific spawn assignments
        ExactFeather.registerSpawn((event) -> {
            if(event.getName() == null) return;
            event.getSpawns().addSpawn(MobCategory.CREATURE,
                    new MobSpawnSettings.SpawnerData(he.get(),
                            hi.spawnWeight(),1,3));
        });
        // register spawning placements
        ExactFeather.registerCommon((event) -> {
            SpawnPlacements.register(he.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.WORLD_SURFACE, hi::canSpawn);
        });
        return Registries.entities.register(name, he);
    }

    public RegistryObject<Item> regEggItem(
            RegistryObject<EntityType<HogEntity>> entity, CreativeModeTab tab
            /* int bg, int fg, String texture */) {
        printClassWrong(Registries.entities, entity.getId().getPath());
        return Registries.items.register(entity.getId().getPath() + "_spawn_egg",
                () -> new ForgeSpawnEggItem(entity, colors(entity, false),
                        colors(entity, true),
                        new Item.Properties().tab(tab).stacksTo(64)));
    }

    public RegistryObject<Potion> regPotionImmediate(String name,
                                                     RegistryObject<Item> add,
                                                     MobEffectCommon... does) {
        printClassWrong(Registries.potions, name);
        RegistryObject<Potion> potion;
        if(does != null) {
            potion = Registries.potions.register(name,
                    () -> new Potion(does));
        } else {
            potion = Registries.potions.register(name,
                    Potion::new);
        }
        ExactFeather.registerCommon((event) -> BrewingRecipeRegistry.addRecipe(new BrewingCommon(
                Potions.WATER, add, potion
        )));
        return potion;
    }

    public RegistryObject<Potion> regPotionSecondary(String name, RegistryObject<Potion> in,
                                                     Item add,
                                                     MobEffectCommon... does) {
        printClassWrong(Registries.potions, name);
        RegistryObject<Potion> potion;
        if(does != null) {
             potion = Registries.potions.register(name,
                    () -> new Potion(does));
        } else {
            potion = Registries.potions.register(name,
                    Potion::new);
        }
        ExactFeather.registerCommon((event) -> BrewingRecipeRegistry.addRecipe(new BrewingCommon(
                in.get(), add, potion
        )));
        return potion;
    }

    public RegistryObject<Potion> regPotionCorrupt(String name, RegistryObject<Potion> in,
                                                   MobEffectCommon... does) {
        printClassWrong(Registries.potions, name);
        RegistryObject<Potion> potion;
        if(does != null) {
            potion = Registries.potions.register(name,
                    () -> new Potion(does));
        } else {
            potion = Registries.potions.register(name,
                    Potion::new);
        }
        ExactFeather.registerCommon((event) -> BrewingRecipeRegistry.addRecipe(new BrewingCommon(
                in.get(), Items.FERMENTED_SPIDER_EYE, potion
        )));
        return potion;
    }

    public RegistryObject<Potion> regAlchemyBase(String name, Item item) {
        printClassWrong(Registries.potions, name);
        RegistryObject<Potion> potion = Registries.potions.register(name,
                Potion::new);
        ExactFeather.registerCommon((event) -> BrewingRecipeRegistry.addRecipe(new BrewingCommon(
                Potions.WATER, item, potion
        )));
        return potion;
    }

    public RegistryObject<SoundEvent> regSound(String name) {
        printClassWrong(Registries.sounds, name);
        return Registries.sounds.register(name,
                () -> new SoundEvent(new ResourceLocation(ExactFeather.MOD_ID, name)));
    }

    private void printClassWrong(RegistryMap<?> test, String name) {
        if(this != test) ExactFeather.LOGGER.warn(this.getClass().getCanonicalName()
                + " is not the " + test.getClass().getCanonicalName() + " registry for " + name);
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
    public @NotNull Set<String> keySet() {
        getEntries();
        return super.keySet();
    }

    @Override
    public @NotNull Collection<Supplier<? extends T>> values() {
        getEntries();
        return super.values();
    }

    @Override
    public @NotNull Set<Entry<String, Supplier<? extends T>>> entrySet() {
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
        throw new UnsupportedOperationException(this.getClass() + ": is a RegistryMap!");
    }
}
