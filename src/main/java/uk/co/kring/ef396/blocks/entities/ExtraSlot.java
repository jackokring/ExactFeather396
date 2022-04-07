package uk.co.kring.ef396.blocks.entities;

public enum ExtraSlot {

    FUEL(true);

    private boolean fuel;// decides if slot is fuel for energy

    ExtraSlot(boolean fuel) {
        this.fuel = fuel;
    }

    public boolean isFuel() {
        return fuel;
    }
}
