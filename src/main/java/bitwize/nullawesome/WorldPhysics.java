package bitwize.nullawesome;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;

public class WorldPhysics {

    public static enum State {
	GROUNDED,
	FALLING
    }
    public int stageEid;
    public boolean facingRight;
    public State state = State.GROUNDED;
    public PointF gravity = new PointF(0.f, 0.2f);
    public PointF thrust = new PointF(0.f, 0.f);
    public float gvel;
    public float gaccel;
    public float gvelmax;
    public float fallmax = 8.f;
    public Rect hitbox = new Rect(0, 0, 0, 0);
    public float radius;
}
