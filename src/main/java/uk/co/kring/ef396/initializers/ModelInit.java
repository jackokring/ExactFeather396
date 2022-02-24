package uk.co.kring.ef396.initializers;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import uk.co.kring.ef396.ExactFeather;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.co.kring.ef396.entities.HogEntity;

@Mod.EventBusSubscriber(modid = ExactFeather.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModelInit {

    @SubscribeEvent
    public static void onCreateAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityInit.HOG.get(), HogEntity.createAttributes().build());
    }
}
