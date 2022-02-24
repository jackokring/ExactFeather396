package uk.co.kring.ef396.utilities;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegistryMap<T extends IForgeRegistryEntry<T>> extends PriorityHashMap<String, T> {

    private final DeferredRegister<T> register;

    public RegistryMap(DeferredRegister<T> register) {
        this.register = register;
    }

    public <I extends T> RegistryObject<I> register(String name, Supplier<? extends I> sup) {
        return register.register(name, sup);
    }

    public Supplier<IForgeRegistry<T>> makeRegistry(String name, Supplier<RegistryBuilder<T>> sup) {
        return register.makeRegistry(name, sup);
    }

    public void register(IEventBus bus) {
        register.register(bus);
    }

    public Collection<RegistryObject<T>> getEntries() {
        //lazy defer
        var entries = register.getEntries();
        entries.forEach(
                registryObject -> {
                    T object = registryObject.get();
                    String key = registryObject.getId().toString();
                    super.put(key, object);
                }
        );
        return entries;
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
    public T get(Object key) {
        getEntries();
        return super.get(key);
    }

    @Override
    public T put(String key, T value) {
        exception();
        return null;
    }

    @Override
    public boolean containsKey(Object key) {
        getEntries();
        return super.containsKey(key);
    }

    @Override
    public T remove(Object key) {
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
    public Collection<T> values() {
        getEntries();
        return super.values();
    }

    @Override
    public Set<Entry<String, T>> entrySet() {
        getEntries();
        return super.entrySet();
    }

    @Override
    public T getOrDefault(Object key, T defaultValue) {
        getEntries();
        return super.getOrDefault(key, defaultValue);
    }

    @Override
    public T putIfAbsent(String key, T value) {
        exception();
        return null;
    }

    @Override
    public boolean remove(Object key, Object value) {
        exception();
        return false;
    }

    @Override
    public boolean replace(String key, T oldValue, T newValue) {
        exception();
        return false;
    }

    @Override
    public T replace(String key, T value) {
        exception();
        return null;
    }

    @Override
    public T computeIfAbsent(String key, Function<? super String, ? extends T> mappingFunction) {
        exception();
        return null;
    }

    @Override
    public T computeIfPresent(String key, BiFunction<? super String, ? super T, ? extends T> remappingFunction) {
        exception();
        return null;
    }

    @Override
    public T compute(String key, BiFunction<? super String, ? super T, ? extends T> remappingFunction) {
        exception();
        return null;
    }

    @Override
    public T merge(String key, T value, BiFunction<? super T, ? super T, ? extends T> remappingFunction) {
        exception();
        return null;
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super T> action) {
        getEntries();
        super.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super T, ? extends T> function) {
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
