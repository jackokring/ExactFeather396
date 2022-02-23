package uk.co.kring.ef396.entities;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uk.co.kring.ef396.ExactFeather;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import uk.co.kring.ef396.entities.model.HogModel;
import uk.co.kring.ef396.entities.render.HogRenderer;
import uk.co.kring.ef396.init.EntityInit;

@Mod.EventBusSubscriber(modid = ExactFeather.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeFrontendEvents {

    @SubscribeEvent
    private void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HogModel.LAYER_LOCATION, HogModel::createBodyLayer);
    }

    @SubscribeEvent
    private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityInit.HOG.get(), HogRenderer::new);
    }
}
