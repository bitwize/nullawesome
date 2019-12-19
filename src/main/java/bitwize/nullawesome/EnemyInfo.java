package bitwize.nullawesome;
import java.util.EnumMap;
import android.graphics.*;

public class EnemyInfo {
    public static final int SEES_TARGET = 1;
    EnemyType type;
    EnemyState currentState;
    EnumMap<EnemyState, EntityProcessor> stateActions = new EnumMap<EnemyState, EntityProcessor>(EnemyState.class);
    EnumMap<EnemyState, EnemyStateTransition[]> script = new EnumMap<EnemyState, EnemyStateTransition[]>(EnemyState.class);
    int targetEid;
    int flags;
    public void scriptSet(EnemyState st, EnemyStateTransition... scr) {
	this.script.put(st, scr);
    }
    public void scriptClear(EnemyState st) {
	this.script.remove(st);
    }
}
