package uk.co.kring.ef396.entity.render;

import net.minecraft.resources.ResourceLocation;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.entity.model.HogModel;
import uk.co.kring.ef396.entities.HogEntity;
import net.minecraft.client.renderer.entity.MobRenderer;

public class HogRenderer extends MobRenderer<HogEntity, HogModel<HogEntity>> {

    protected static final ResourceLocation TEXTURE = new ResourceLocation(ExactFeather.MOD_ID, "textures/entity/hog.png");

    public HogRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new HogModel<>(), 0.7f);
    }

    @Override
    public ResourceLocation getEntityTexture(HogEntity entity) {
        return TEXTURE;
    }
}
