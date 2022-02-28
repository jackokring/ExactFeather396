package uk.co.kring.ef396.entities;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;

public interface LayerHandler {
    /* Implemented is in the `Model`.
       ==============================

       The `Renderer<Entity, Model<Entity>>` seems to have complete generic access for visual expressiveness requests.
       The `Entity` or Type, is behaviourism and qualities of operational concern.
       The `Model<Entity>` is where the particular drawing is done.

       The `Renderer` can get the `Model`, and could choose alternates if desired. The context is
       decided by the renderer.
       The `Model` does a basic GPU fill based on its implementation, and animation too. I assume called by
       the default renderer.
       The `Entity` runs the MVC model. Supplies data via some mechanism to the renderer and model.
     */

    public static ModelLayerLocation getLayerLocation() {
        throw new UnsupportedOperationException("LayerHandler.getLayerLocation() not implemented!");
    }

    public static LayerDefinition createBodyLayer() {
        throw new UnsupportedOperationException("LayerHandler.createBodyLayer() not implemented!");
    }

    public static AttributeSupplier.Builder createAttributes() {
        throw new UnsupportedOperationException("LayerHandler.createAttributes() not implemented!");
    }

    public static float hitX() {
        return 1.0f;
    }

    public static float hitY() {
        return 1.0f;
    }
}
