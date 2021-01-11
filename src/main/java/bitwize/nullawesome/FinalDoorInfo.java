package bitwize.nullawesome;

/* FinalDoors do not work like regular Doors. They do not block access
 * to other parts of the level, but rather serve as an exit to the
 * level. */

public class FinalDoorInfo {
    public static final int MAX_OPEN = 8;
    public static final int MAX_DELAY = 3;
    public static final short GOAL_TILE_START = 28;
    public static final int FD_WIDTH=54;
    public static final int FD_HEIGHT=54;
    public static final float FD_HOTSPOT_X=27.f;
    public static final float FD_HOTSPOT_Y=27.f;
    public int open = 0;
    public int delay = 0;
    public DoorState state = DoorState.CLOSED;
}
