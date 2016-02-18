package bitwize.nullawesome;

import android.graphics.Bitmap;
import android.graphics.PointF;

public class WorldPhysics {

    public static enum State {
	GROUNDED,
	FALLING
    }
    public int stageEid;
    public boolean facingRight;
    public State state = State.GROUNDED;
    public PointF gravity = new PointF(0.f, 0.1f);
    public PointF thrust = new PointF(0.f, 0.f);
    public float gvel;
    public float gaccel;
    public float gvelmax;
    public float fallmax = 3.f;
    public float radius;
}
