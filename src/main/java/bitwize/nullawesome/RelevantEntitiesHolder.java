package bitwize.nullawesome;

import java.util.HashSet;

public class RelevantEntitiesHolder {
    private HashSet<Integer> ents = new HashSet<Integer>();
    private Criterion relevance;
    private EntityProcessor adder = (eid) -> {
        if(this.relevance.test(eid)) {
            ents.add(eid);
        }
    };
    private EntityProcessor remover = (eid) -> {
        ents.remove(eid);
    };
    public RelevantEntitiesHolder(Criterion aCriterion) {
        this.relevance = aCriterion;
        EntityRepository.get().processEntities(adder);
    }

    public void register() {
        EntityRepository.get().registerHooks(adder, remover);
    }

    public void unregister() {
        EntityRepository.get().unregisterHooks(adder, remover);
    }
    public void processAll(EntityProcessor proc) {
        for(int eid : ents) {
            proc.process(eid);
        }
    }

    public static Criterion hasComponentCriterion(Class<?> aClass) {
        return (eid) -> EntityRepository.get().getComponent(eid, aClass) != null;
    }
}
