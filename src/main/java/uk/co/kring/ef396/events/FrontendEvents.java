package uk.co.kring.ef396.events;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import uk.co.kring.ef396.ExactFeather;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import uk.co.kring.ef396.entity.model.HogModel;

@Mod.EventBusSubscriber(modid = ExactFeather.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class FrontendEvents {

    @SubscribeEvent
    private void setup(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HogModel.LAYER_LOCATION, HogModel::createBodyLayer);
    }
}
