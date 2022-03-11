package uk.co.kring.ef396.recipes;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.HashMap;
import java.util.Map;

public class MobEffectCommon extends MobEffectInstance {

    public MobEffectCommon(MobEffect effect) {
        this(effect, 3600, 0);
    }

    public MobEffectCommon(MobEffect effect, int time, int level) {
        super(effect, time, level);
        this.time = time;
        this.level = level;
        this.effect = effect;
    }

    private int time;
    private int level;
    private MobEffect effect;

    private final static MobEffect[] keys = {
            MobEffects.MOVEMENT_SPEED,
            MobEffects.MOVEMENT_SLOWDOWN,
            MobEffects.DIG_SPEED,
            MobEffects.DIG_SLOWDOWN,

            MobEffects.DAMAGE_BOOST,
            MobEffects.HEAL,
            MobEffects.HARM,
            MobEffects.JUMP,

            MobEffects.CONFUSION,
            MobEffects.REGENERATION,
            MobEffects.DAMAGE_RESISTANCE,
            MobEffects.FIRE_RESISTANCE,

            MobEffects.WATER_BREATHING,
            MobEffects.INVISIBILITY,
            MobEffects.BLINDNESS,
            MobEffects.NIGHT_VISION,

            MobEffects.HUNGER,
            MobEffects.WEAKNESS,
            MobEffects.POISON,
            MobEffects.WITHER,

            MobEffects.HEALTH_BOOST,
            MobEffects.ABSORPTION,
            MobEffects.SATURATION,
            MobEffects.GLOWING,

            MobEffects.LEVITATION,
            MobEffects.LUCK,
            MobEffects.UNLUCK,
            MobEffects.SLOW_FALLING,

            MobEffects.CONDUIT_POWER,
            MobEffects.DOLPHINS_GRACE,
            MobEffects.BAD_OMEN,
            MobEffects.HERO_OF_THE_VILLAGE
    };

    private final static MobEffect[] values = {
            //TODO mark OK when converted to opposite in group of 4
            //OK
            MobEffects.MOVEMENT_SLOWDOWN,//
            null,
            null,
            null,

            MobEffects.DAMAGE_BOOST,
            MobEffects.HARM,//
            null,
            MobEffects.MOVEMENT_SLOWDOWN,//

            MobEffects.CONFUSION,//
            MobEffects.REGENERATION,
            MobEffects.DAMAGE_RESISTANCE,
            MobEffects.FIRE_RESISTANCE,

            MobEffects.WATER_BREATHING,
            MobEffects.INVISIBILITY,
            MobEffects.BLINDNESS,
            MobEffects.INVISIBILITY,//

            MobEffects.HUNGER,
            MobEffects.WEAKNESS,
            MobEffects.HARM,//
            MobEffects.WITHER,

            MobEffects.HEALTH_BOOST,
            MobEffects.ABSORPTION,
            MobEffects.SATURATION,
            MobEffects.GLOWING,

            //OK
            null,
            MobEffects.UNLUCK,//
            MobEffects.LUCK,//
            null,

            MobEffects.CONDUIT_POWER,
            MobEffects.DOLPHINS_GRACE,
            MobEffects.BAD_OMEN,
            MobEffects.HERO_OF_THE_VILLAGE
    };

    public static final Map<MobEffect, MobEffect> opposites = new HashMap<>();

    static {
        for(int i = 0; i < keys.length; i++) {
            opposites.put(keys[i], values[i]);
        }
    }

    @Override
    public boolean equals(Object e) {
        if(e instanceof MobEffectCommon) {
            MobEffectCommon f = (MobEffectCommon) e;
            return  f.effect == effect && f.level == level && f.time == time;
        } else {
            return false;
        }
    }

    public MobEffectCommon corrupt(boolean effCorrupt, boolean redTime, boolean glowLevel) {
        return new MobEffectCommon(
                effCorrupt ? (opposites.get(effect) == null ? effect : opposites.get(effect)) : effect,
                redTime ? time * 8 / 3 : time,
                glowLevel ? level + 1 : level);
    }
}
