package bitwize.nullawesome;
import java.util.EnumMap;
import android.graphics.*;

public class EnemyInfo {
    public static final int SEES_TARGET = 1;
    public static final int CAN_JUMP = 2;
    public static final int IRVISION=4;
    public static final int LIDAR=8;
    public EnemyType type;
    public EnemyState currentState;
    public EnumMap<EnemyState, EntityProcessor> stateActions = new EnumMap<EnemyState, EntityProcessor>(EnemyState.class);
    public EnumMap<EnemyState, EnemyStateTransition[]> script = new EnumMap<EnemyState, EnemyStateTransition[]>(EnemyState.class);
    public int targetEid;
    public PointF targetLastKnownPosition = new PointF();
    public int flags;
    public int pauseTimer = 0;
    public float walkVel;
    public float chaseVel;
    public float sightRange;
    public float sightFrustum;
    public float fireRange;
    public float distanceToTarget;
    public int fireCooldown;
    public int fireTimer = 0;

    public void scriptSet(EnemyState st, EnemyStateTransition... scr) {
	this.script.put(st, scr);
    }
    public void scriptClear(EnemyState st) {
	this.script.remove(st);
    }
}
