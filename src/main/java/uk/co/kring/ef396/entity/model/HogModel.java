package uk.co.kring.ef396.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import uk.co.kring.ef396.entities.HogEntity;

public class HogModel<T extends HogEntity> extends EntityModel<T> {

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart legFrontRight;
    private final ModelPart legFrontLeft;
    private final ModelPart legBackRight;
    private final ModelPart legBackLeft;

    public final int textureWidth = 64;
    public final int textureHeight = 32;

    public HogModel() {

        head = new ModelPart(this);
        head.setPos(0.0F, 12.0F, -6.0F);
        head.texOffs(0, 0).addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, 0.0F, false);
        head.texOffs(16, 16).addBox(-2.0F, 0.0F, -9.0F, 4.0F, 3.0F, 1.0F, 0.0F, false);

        body = new ModelPart(this);
        body.setPos(0.0F, 11.0F, 2.0F);
        setRotationAngle(body, 1.5708F, 0.0F, 0.0F);
        body.texOffs(28, 8).addBox(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, 0.0F, false);

        legFrontRight = new ModelPart(this);
        legFrontRight.setRotationPoint(3.0F, 18.0F, -5.0F);
        legFrontRight.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.0F, false);

        legFrontLeft = new ModelPart(this);
        legFrontLeft.setRotationPoint(-3.0F, 18.0F, -5.0F);
        legFrontLeft.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.0F, false);

        legBackRight = new ModelPart(this);
        legBackRight.setRotationPoint(3.0F, 18.0F, 7.0F);
        legBackRight.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.0F, false);

        legBackLeft = new ModelPart(this);
        legBackLeft.setRotationPoint(-3.0F, 18.0F, 7.0F);
        legBackLeft.texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, 0.0F, false);
    }

    @Override
    public void renderToBuffer(PoseStack PoseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
        head.render(PoseStack, buffer, packedLight, packedOverlay);
        body.render(PoseStack, buffer, packedLight, packedOverlay);
        legFrontRight.render(PoseStack, buffer, packedLight, packedOverlay);
        legFrontLeft.render(PoseStack, buffer, packedLight, packedOverlay);
        legBackRight.render(PoseStack, buffer, packedLight, packedOverlay);
        legBackLeft.render(PoseStack, buffer, packedLight, packedOverlay);
    }

    public void setRotationAngle(ModelPart ModelPart, float x, float y, float z) {
        ModelPart.xRot = x;
        ModelPart.yRot = y;
        ModelPart.zRot = z;
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.xRot = headPitch * ((float)Math.PI / 180F);
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.body.xRot = ((float)Math.PI / 2F);
        this.legBackRight.xRot = (float)Math.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.legBackLeft.xRot = (float)Math.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.legFrontRight.xRot = (float)Math.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.legFrontLeft.xRot = (float)Math.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
    }
}
