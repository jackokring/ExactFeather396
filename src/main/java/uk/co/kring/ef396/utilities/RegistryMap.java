package uk.co.kring.ef396.utilities;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Supplier;

public class RegistryMap<T extends IForgeRegistryEntry<T>> extends HashMap<String, T> {

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
                    this.put(key, object);
                }
        );
        return entries;
    }
}
