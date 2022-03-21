package uk.co.kring.ef396.blocks.entities;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import uk.co.kring.ef396.ExactFeather;

public class EnergyScreen extends AbstractContainerScreen<EnergyContainer> {

    protected static ResourceLocation GUI;

    private void setGUI(String name) {
        GUI = new ResourceLocation(ExactFeather.MOD_ID,
                "textures/gui/" + name + ".png");//set via path
    }

    public EnergyScreen(EnergyContainer container, Inventory inv, Component name) {
        // name is translated already as is a component
        super(container, inv, name);
        setGUI(container.getBlockSingleton().getId().getPath());//via instance
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
