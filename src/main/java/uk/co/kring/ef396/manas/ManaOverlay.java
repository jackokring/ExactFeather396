package uk.co.kring.ef396.manas;

import net.minecraftforge.client.gui.IIngameOverlay;

public class ManaOverlay {//OK as GFX

    public static final IIngameOverlay HUD_MANA = (gui, poseStack, partialTicks, width, height) -> {
        String toDisplay = getPlayerMana() + " / " + getChunkMana();
        int x = ManaConfig.MANA_HUD_X.get();
        int y = ManaConfig.MANA_HUD_Y.get();
        if (x >= 0 && y >= 0) {
            gui.getFont().draw(poseStack, toDisplay, x, y, ManaConfig.MANA_HUD_COLOR.get());
        }
    };

    private static int playerMana;
    private static int chunkMana;

    public static void set(int playerManaSet, int chunkManaSet) {
        playerMana = playerMana;
        chunkMana = chunkMana;
    }

    public static int getPlayerMana() {
        return playerMana;
    }

    public static int getChunkMana() {
        return chunkMana;
    }
}
