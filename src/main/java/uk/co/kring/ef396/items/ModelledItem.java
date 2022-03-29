package uk.co.kring.ef396.items;

import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;

import java.util.Objects;

public interface ModelledItem {

    default void provideModel(ItemModelProvider imp, Item i) {
        //use to make data generation not make an auto
        imp.singleTexture(Objects.requireNonNull(i.getRegistryName()).getPath(),
                imp.mcLoc("item/generated"),
                "layer0", imp.modLoc("item/" + i.getRegistryName().getPath()));
    }
}
