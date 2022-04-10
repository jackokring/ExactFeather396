package uk.co.kring.ef396.blocks.entities;

import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;

public abstract class CraftyStackHandler extends ItemStackHandler {

    public CraftyStackHandler(int size) {
        super(size);
    }

    public abstract void addSlots();

    protected void addSlot(ExtraSlot slot) {
        quickCrafty.add(slot);
    }

    private ArrayList<ExtraSlot> quickCrafty = new ArrayList<>();

    public ArrayList<ExtraSlot> getQuickCrafty() {
        return quickCrafty;
    }
}
