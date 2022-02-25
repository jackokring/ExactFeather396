package uk.co.kring.ef396.entities;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

public interface AttributeCreator {
    public static AttributeSupplier.Builder createAttributes() {
        throw new UnsupportedOperationException("AttributeCreator not implemented!");
    }
}
