package uk.co.kring.ef396.items;

import net.minecraftforge.client.model.generators.ItemModelProvider;

public interface ModelledItem {

    default void provideModel(ItemModelProvider itemModelProvider) {
        //use to make data generation not make an auto
    }
}
