package uk.co.kring.ef396.entities.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
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

    @Override
    public void renderToBuffer(PoseStack PoseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){

    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }
}
