package uk.co.kring.ef396.data.mini;

import java.awt.event.KeyEvent;

public enum Keys {
    //Standard MAME
    COIN_SELECT_5(KeyEvent.VK_5),
    PLAYER_START_1(KeyEvent.VK_1),
    UP(KeyEvent.VK_UP),
    DOWN(KeyEvent.VK_DOWN),
    LEFT(KeyEvent.VK_LEFT),
    RIGHT(KeyEvent.VK_RIGHT),
    SWAP_B_CTRL(KeyEvent.VK_CONTROL),
    FIRE_A_ALT(KeyEvent.VK_ALT),
    ALARM_X_SPACE(KeyEvent.VK_SPACE),
    COMBO_Y_SHIFT(KeyEvent.VK_SHIFT),
    TELE_L_Z(KeyEvent.VK_Z),
    WIDE_R_X(KeyEvent.VK_X),
    SAVE_CHEAT_C(KeyEvent.VK_C),
    LOAD_VERSION_V(KeyEvent.VK_V),
    SYS_PAUSE_P(KeyEvent.VK_P),
    SYS_ADMIN_ENTER(KeyEvent.VK_ENTER);

    //Then use Qjoypad.

    private final int key;
    private boolean pressed = false;
    private boolean last = false;

    Keys(int event) {
        this.key = event;
    }

    public int getKeyCode() {
        return key;
    }

    public void pressKey() {
        last = pressed;
        pressed = true;
    }

    public void releaseKey() {
        last = pressed;
        pressed = false;
    }

    public boolean getKey() {
        return pressed;
    }

    public boolean downKey() {
        return pressed && !last;
    }

    public boolean upKey() {
        return !pressed && last;
    }
}
