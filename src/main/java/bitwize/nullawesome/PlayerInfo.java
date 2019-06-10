package bitwize.nullawesome;

import android.graphics.*;

public class PlayerInfo {
    // Key state values
    public static final int KEY_LEFT = 1;
    public static final int KEY_RIGHT = 2;
    public static final int KEY_JUMP = 4;
    public static final int KEY_HACK = 8;
    public static final int KEY_PAUSE = 16;
    public static final int KEY_BACK = 128;
    // Player flag values

    //   Flag to track whether a jump was already made per jump button
    //   press (so Lorn doesn't have moon boots on when the jump
    //   button is held)

    public static final int JUMPED = 1;

    //   This flag is set if Lorn is dead (he took a hit or a fatal
    //   fall)
    
    public static final int DEAD = 2;

    public int keyStatus = 0;
    public InputState inputState = InputState.MOVEMENT;
    public int flags = 0;
}
