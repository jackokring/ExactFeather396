package uk.co.kring.ef396.recipes;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class MobEffectCommon extends MobEffectInstance {

    public MobEffectCommon(MobEffect effect, int level) {
        super(effect, 300, level);
    }
}
