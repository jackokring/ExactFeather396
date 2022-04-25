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

    public enum Combo {
        NONE(null, null, true, true, true);

        private final Combo old;
        private final Keys key;
        private final boolean onOff;
        private final boolean axisOK;
        private final boolean exit;

        Combo(Combo old, Keys key, boolean onOff, boolean axisOK, boolean exit) {
            this.old = old;
            this.key = key;
            this.onOff = onOff;
            this.axisOK = axisOK;
            this.exit = exit;
        }
    }

    private final int key;
    private boolean pressed = false;
    private boolean last = false;
    private int repeats;

    private static int vx, vy;
    public static final int velMax = 1024;
    public static final int invAccel = 64;//divide vel from top by
    public static final int invDecel = 64;//divide by exponential

    private static Combo combo = Combo.NONE;
    private static boolean comboUsed = false;

    Keys(int event) {
        this.key = event;
    }

    public int getKeyCode() {
        return key;
    }

    public void pressKey() {
        pressed = true;
        repeats = (int)System.currentTimeMillis();
        if(comboUsed) for(Combo c: Combo.values()) {
            if(c.old != combo) continue;
            if(c.key == this && c.onOff) {
                combo = c;
                break;
            }
        }
    }

    public void releaseKey() {
        pressed = false;
        if(comboUsed) for(Combo c: Combo.values()) {
            if(c.old != combo) continue;
            if(c.key == this && !c.onOff) {
                combo = c;
                break;
            }
        }
    }

    public static void setComboCanBeUsed(boolean comboUsed) {
        Keys.comboUsed = comboUsed;
        if(!comboUsed) resetComboAsUsed();
    }

    public static void resetComboAsUsed() {
        combo = Combo.NONE;
    }

    public static Combo getCombo() {
        if(combo.exit) resetComboAsUsed();
        return combo;
    }

    public boolean getKey() {
        last = pressed;
        return pressed;
    }

    public boolean getKeyRepeats() {
        return getKey() && ((System.currentTimeMillis() - repeats & 256) == 0);
    }

    public boolean onKey() {
        return !last && getKey();
    }

    public boolean offKey() {
        return last && !getKey();
    }

    public static int axisX() {
        int x = 0;
        if(combo.axisOK) {
            if (Keys.LEFT.getKey()) x--;
            if (Keys.RIGHT.getKey()) x++;
        }
        if(x == 0) {
            vx /= invDecel;
        } else {
            int max = velMax - ((vx < 0) ? -vx : vx);
            if (-velMax < vx && vx < velMax) {
                vx += max / invAccel * x;
            }
        }
        return vx;
    }

    public static int axisY() {
        int y = 0;
        if(combo.axisOK) {
            if (Keys.UP.getKey()) y--;
            if (Keys.DOWN.getKey()) y++;
        }
        if(y == 0) {
            vy /= invDecel;
        } else {
            int max = velMax - ((vy < 0) ? -vy : vy);
            if (-velMax < vy && vy < velMax) {
                vy += max / invAccel * y;
            }
        }
        return vy;
    }
}
