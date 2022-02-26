package uk.co.kring.ef396.entities;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

public interface LayerHandler {

    public static ModelLayerLocation getLayerLocation() {
        throw new UnsupportedOperationException("LayerHandler.getLayerLocation() not implemented!");
    }

    public static LayerDefinition createBodyLayer() {
        throw new UnsupportedOperationException("LayerHandler.createBodyLayer() not implemented!");
    }

    public static AttributeSupplier.Builder createAttributes() {
        throw new UnsupportedOperationException("LayerHandler.createAttributes() not implemented!");
    }
}
