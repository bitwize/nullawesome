package bitwize.nullawesome;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;

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
    public float gvel = 0.f;
    public float gaccel = 0.f;
    public float gvelmax = 2.f;
    public float fallmax = 8.f;
    public RectF hitbox = new RectF(0.f, 0.f, 0.f, 0.f);
    public float radius = 0.f;
    public int sticksToEid = EntityRepository.NO_ENTITY;
    public PointF sticksToPosition = new PointF();
    public CollisionUpdateAgent.Criterion collisionCriterion = CollisionUpdateAgent.nullCriterion;
    public CollisionUpdateAgent.Collider collider = CollisionUpdateAgent.nullCollider;
}
