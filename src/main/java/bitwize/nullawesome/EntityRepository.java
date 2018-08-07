package bitwize.nullawesome;

import java.util.HashMap;
import java.util.Map;
import java.util.BitSet;

public class EntityRepository {
    public static final int MAX_ENTITIES = 8192;
    public static final int NO_ENTITY = -1;
    private HashMap<Class<?>, Object[]> componentArrays;
    private BitSet active;
    private int lastEid = 0;
    private int maxEid = 0;
    private static EntityRepository theInstance = new EntityRepository();

    private EntityRepository() {
	componentArrays = new HashMap<Class<?>, Object[]>();
	active = new BitSet();
    }

    public static EntityRepository get() {
	return theInstance;
    }

    public static void createInstance() {
	theInstance = new EntityRepository();
    }

    public boolean hasEntity(int eid) {
	return active.get(eid);
    }

    public Object getComponent(int eid, Class<?> kls) {
	if(!active.get(eid)) {
	    return null;
	}
	if(!(componentArrays.containsKey(kls))) {
	    return null;
	}
	return (componentArrays.get(kls))[eid];
    }
    
    private int getNextEid() throws EntityTableFullException {
	int marker = lastEid;
	while(hasEntity(lastEid)) {
	    lastEid++;
	    if(lastEid >= MAX_ENTITIES) lastEid = 0;
	    if(lastEid == marker) throw new EntityTableFullException();
	}
	if(maxEid < lastEid) {
	    maxEid = lastEid;
	}
	return lastEid;
    }

    public int newEntity() throws EntityTableFullException {
	int eid = getNextEid();
	active.set(eid, true);
	return eid;
    }
    public void removeEntity(int eid) {
	if(hasEntity(eid)) {
	    active.set(eid, false);
	    for(Class<?> c : componentArrays.keySet()) {
		(componentArrays.get(c))[eid] = null;
	    }
	}
	for(int i=maxEid; i>=0; i--) {
	    if(active.get(i)) { maxEid = i; break; }
	}
    }

    public void addComponent(int eid, Object comp) {
	Class<?> klass = comp.getClass();
	if(!active.get(eid)) {
	    return;
	}
	if(!componentArrays.containsKey(klass)) {
	    componentArrays.put(klass, new Object[MAX_ENTITIES]);
	}
	(componentArrays.get(klass))[eid] = comp;
    }

    public void processEntitiesWithComponent(Class<?> klass, EntityProcessor p) {
	for(int i=0;i<=maxEid;i++) {
	    if(active.get(i) && ((componentArrays.get(klass))[i] != null)) {
		p.process(i);
	    }
	}
    }

    public int findEntityWithComponent(Class<?> klass) {
	for(int j=0;j<=maxEid;j++) {
	    if(active.get(j) && ((componentArrays.get(klass))[j] != null)) return j;
	}
	return NO_ENTITY;
    }
}
