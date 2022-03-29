package uk.co.kring.ef396.manas;

import net.minecraft.nbt.CompoundTag;

public class Mana {// OK, basic load/save mana persistence holder
    private int mana;

    public Mana(int mana) {
        setMana(mana);
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void addMana(int mana) {
        this.mana += mana;
    }

    public void copyFrom(Mana source) {
        mana = source.mana;
    }


    public void saveNBTData(CompoundTag compound) {
        compound.putInt("mana", mana);
    }

    public void loadNBTData(CompoundTag compound) {
        mana = compound.getInt("mana");
    }
}
