package bitwize.nullawesome;

import android.graphics.RectF;
import android.graphics.PointF;

public class HackTarget {
    float width;
    float height;
    int requiresKeyMask;
    byte hasKey;
    boolean hacked = false;
    boolean visible = false;
    EntityProcessor action;
    int linkedThingIndex = -1;
}
