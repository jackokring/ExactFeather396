package uk.co.kring.ef396.utilities;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;
import uk.co.kring.ef396.ExactFeather;

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

    public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> sup,
                                                    Consumer<Configurator.Builder> user) {
        Configurator.configRegistryEntry(register, name, user);
        return register.register(name, sup);
    }

    // TODO put name manglers here??
    // minecraft:diamond -> item.minecraft.diamond
    // as full index accessed via type before namespace

    public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> sup) {
        return register.register(name, sup);
    }

    public Supplier<IForgeRegistry<T>> makeRegistry(String name, Supplier<RegistryBuilder<T>> sup) {
        return register.makeRegistry(name, sup);
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
