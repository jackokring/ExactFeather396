package uk.co.kring.ef396.entities.initials;

import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class UnitaryAttribute extends RangedAttribute {

    public UnitaryAttribute(String name, double value) {
        super(name, value, -1.0, 1.0);
    }
}
