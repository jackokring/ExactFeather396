package uk.co.kring.ef396.initializers;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import uk.co.kring.ef396.ExactFeather;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.co.kring.ef396.entities.models.HogModel;

@Mod.EventBusSubscriber(modid = ExactFeather.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModelInit {

    @SubscribeEvent
    public static void onCreateAttributes(EntityAttributeCreationEvent event) {
        //This event is fired after registration and before common setup.
        event.put(EntityInit.HOG.get(), HogModel.createAttributes().build());
    }
}
