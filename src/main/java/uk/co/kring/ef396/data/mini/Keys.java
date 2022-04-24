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
    private static int vx, vy;
    public static final int velMax = 1024;
    public static final int invAccel = 64;//divide vel from top by
    public static final int invDecel = 64;//divide by exponential

    Keys(int event) {
        this.key = event;
    }

    public int getKeyCode() {
        return key;
    }

    public void pressKey() {
        pressed = true;
    }

    public void releaseKey() {
        pressed = false;
    }

    public boolean getKey() {
        last = pressed;
        return pressed;
    }

    public boolean onKey() {
        return !last && getKey();
    }

    public boolean offKey() {
        return last && !getKey();
    }

    public static int axisX() {
        int x = 0;
        if(Keys.LEFT.getKey()) x--;
        if(Keys.RIGHT.getKey()) x++;
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
        if(Keys.UP.getKey()) y--;
        if(Keys.DOWN.getKey()) y++;
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
