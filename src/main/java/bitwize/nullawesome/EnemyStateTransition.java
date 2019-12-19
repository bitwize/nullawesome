package bitwize.nullawesome;

public class EnemyStateTransition {
    Criterion criterion;
    EnemyState newState;
    public EnemyStateTransition(Criterion c, EnemyState s) { criterion = c; newState = s; }
}
