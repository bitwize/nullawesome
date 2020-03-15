package bitwize.nullawesome;
import java.util.EnumMap;
import android.graphics.*;

public class EnemyInfo {
    public static final int SEES_TARGET = 1;
    public static final int CAN_JUMP = 2;
    public static final int IRVISION=4;
    public static final int LIDAR=8;
    EnemyType type;
    EnemyState currentState;
    EnumMap<EnemyState, EntityProcessor> stateActions = new EnumMap<EnemyState, EntityProcessor>(EnemyState.class);
    EnumMap<EnemyState, EnemyStateTransition[]> script = new EnumMap<EnemyState, EnemyStateTransition[]>(EnemyState.class);
    int targetEid;
    PointF targetLastKnownPosition = new PointF();
    int flags;
    int pauseTimer = 0;
    float walkVel;
    float chaseVel;
    float sightRange;
    float sightFrustum;
    public void scriptSet(EnemyState st, EnemyStateTransition... scr) {
	this.script.put(st, scr);
    }
    public void scriptClear(EnemyState st) {
	this.script.remove(st);
    }
}
