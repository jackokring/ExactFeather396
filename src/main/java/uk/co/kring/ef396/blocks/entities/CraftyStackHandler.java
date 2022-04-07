package uk.co.kring.ef396.blocks.entities;

import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;

public class CraftyStackHandler extends ItemStackHandler {

    public CraftyStackHandler(int size) {
        super(size);
    }

    private ArrayList<ExtraSlot> quickCrafty = new ArrayList<>();

    public ArrayList<ExtraSlot> getQuickCrafty() {
        return quickCrafty;
    }
}
