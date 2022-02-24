package uk.co.kring.ef396.utilities;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.HashMap;

public class RegistryMap<T extends IForgeRegistryEntry<T>> extends HashMap<String, T> {

    public RegistryMap(DeferredRegister<T> register) {
        register.getEntries().forEach(
                registryObject -> {
                    T object = registryObject.get();
                    String key = registryObject.getId().toString();
                    this.put(key, object);
                }
        );
    }
}
