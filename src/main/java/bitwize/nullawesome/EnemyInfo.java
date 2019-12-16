package bitwize.nullawesome;
import java.util.EnumMap;
import android.graphics.*;

public class EnemyInfo {
    EnemyType type;
    EnemyState currentState;
    EnumMap<EnemyState, EntityProcessor> stateActions = new EnumMap<EnemyState, EntityProcessor>(EnemyState.class);
    EnumMap<EnemyState, EnemyStateTransition[]> script = new EnumMap<EnemyState, EnemyStateTransition[]>(EnemyState.class);
    int targetEid;
    boolean seesTarget;
}
