package bitwize.nullawesome;

import java.util.HashMap;
import java.util.Map;
import java.util.BitSet;

public class EntityRepository {
    public static final int MAX_ENTITIES = 8192;
    private HashMap<Class<?>, Object[]> componentArrays;
    private BitSet active;
    private int lastEid = 0;
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

    public Object getComponent(int eid, Class<?> kls)
	throws InvalidEntityException {
	if(!active.get(eid)) {
	    throw new InvalidEntityException(eid);
	}
	if(!(componentArrays.containsKey(kls))) {
	    return null;
	}
	return (componentArrays.get(kls))[eid];
    }
    
    public int getNextEid() throws EntityTableFullException {
	int marker = lastEid;
	while(hasEntity(lastEid)) {
	    lastEid++;
	    if(lastEid >= MAX_ENTITIES) lastEid = 0;
	    if(lastEid == marker) throw new EntityTableFullException();
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
    }

    public void addComponent(int eid, Object comp) {
	Class<?> klass = comp.getClass();
	if(!componentArrays.containsKey(klass)) {
	    componentArrays.put(klass, new Object[MAX_ENTITIES]);
	}
	(componentArrays.get(klass))[eid] = comp;
    }

    public void processEntitiesWithComponent(Class<?> klass, EntityProcessor p) {
	for(int i=0;i<MAX_ENTITIES;i++) {
	    if(active.get(i) && ((componentArrays.get(klass))[i] != null)) {
		p.process(i);
	    }
	}
    }

    public int findEntityWithComponent(Class<?> klass) {
	for(int j=0;j<MAX_ENTITIES;j++) {
	    if(active.get(j) && ((componentArrays.get(klass))[j] != null)) return j;
	}
	return -1;
    }
}
