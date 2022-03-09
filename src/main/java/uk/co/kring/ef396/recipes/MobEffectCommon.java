package uk.co.kring.ef396.recipes;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public class MobEffectCommon extends MobEffectInstance {

    public MobEffectCommon(MobEffect effect) {
        this(effect, 3600, 0);
    }

    public MobEffectCommon(MobEffect effect, int time, int level) {
        super(effect, time, level);
        this.time = time;
        this.level = level;
    }

    private int time;
    private int level;

    public MobEffectCommon corrupt(MobEffect opposite, boolean redTime, boolean glowLevel) {
        return (opposite == null) ? null :
                new MobEffectCommon(opposite, redTime ? time * 8 / 3 : time,
                        glowLevel ? level + 1 : level);
    }
}
