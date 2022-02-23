package uk.co.kring.ef396.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import uk.co.kring.ef396.ExactFeather;

public class SoundInit {

    public static final DeferredRegister<SoundEvent> SOUNDS
            = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ExactFeather.MOD_ID);

    public static final RegistryObject<SoundEvent> NAME
            = SOUNDS.register("name", () -> new SoundEvent(new ResourceLocation(ExactFeather.MOD_ID, "name")));


}
