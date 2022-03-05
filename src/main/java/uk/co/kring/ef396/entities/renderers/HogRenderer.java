package uk.co.kring.ef396.entities.renderers;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HuskRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Zombie;
import uk.co.kring.ef396.ExactFeather;

public class HogRenderer extends HuskRenderer {

    protected static final ResourceLocation TEXTURE =
            new ResourceLocation(ExactFeather.MOD_ID, "textures/entity/hog.png");

    public HogRenderer(EntityRendererProvider.Context p_174456_) {
        super(p_174456_);
    }

    @Override
    public ResourceLocation getTextureLocation(Zombie p_113771_) {
        return TEXTURE;
    }
}
