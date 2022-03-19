package uk.co.kring.ef396.blocks.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import uk.co.kring.ef396.ExactFeather;
import uk.co.kring.ef396.blocks.containers.EnergyContainer;

public class EnergyScreen extends AbstractContainerScreen<EnergyContainer> {

    protected ResourceLocation GUI;

    protected void setGUI(String name) {
        GUI = new ResourceLocation(ExactFeather.MOD_ID,
                "textures/gui/" + name + "_gui.png");
    }

    public EnergyScreen(EnergyContainer container, Inventory inv, Component name) {
        super(container, inv, name);
        setGUI(name.getContents());//to string best
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        drawString(matrixStack, font,
                "Energy: " + menu.getEnergy(), 10, 10, 0xffffff);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }
}
