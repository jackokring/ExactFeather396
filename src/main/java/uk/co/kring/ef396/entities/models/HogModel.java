package uk.co.kring.ef396.entities.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import uk.co.kring.ef396.entities.HogEntity;
import uk.co.kring.ef396.entities.LayerHandler;

public class HogModel<T extends HogEntity> extends EntityModel<T> implements LayerHandler {

    public HogModel() {

    }

    public static ModelLayerLocation getLayerLocation() {
        throw new UnsupportedOperationException("LayerHandler.getLayerLocation() not implemented!");
    }

    public static LayerDefinition createBodyLayer() {
        throw new UnsupportedOperationException("LayerHandler.createBodyLayer() not implemented!");
    }

    public static AttributeSupplier.Builder createAttributes() {
        // most attributes seem to be supplied animator parameters
        // I think it allows typing based on simple model differentiation
        // freeing the entity to process and not be involved with simple variants
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    public static float hitX() {
        return 1.0f;
    }

    public static float hitY() {
        return 1.0f;
    }

    @Override
    public void renderToBuffer(PoseStack PoseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){

    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }
}
