package bitwize.nullawesome;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.BitSet;
import java.util.HashSet;
import java.util.ArrayList;

public class EntityRepository {
    public static final int MAX_ENTITIES = 8192;
    public static final int NO_ENTITY = -1;
    private HashMap<Class<?>, Object[]> componentArrays = new HashMap<Class<?>, Object[]>();;
    private BitSet active = new BitSet();
    private int lastEid = 0;
    private int maxEid = 0;
    private HashSet<EntityProcessor> addHooks = new HashSet<EntityProcessor>();
    private HashSet<EntityProcessor> removeHooks = new HashSet<EntityProcessor>();
    private static EntityRepository theInstance = new EntityRepository();
    
    private EntityRepository() {
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
	    for(EntityProcessor h : removeHooks) {
		h.process(eid);
	    }
	}
	for(int i=maxEid; i>=0; i--) {
	    if(active.get(i)) { maxEid = i; break; }
	}
    }

    public void clear() {
	active.clear();
	componentArrays.clear();
	lastEid = 0;
	maxEid = 0;
	clearHooks();
    }

    public void clearHooks() {
	addHooks.clear();
	removeHooks.clear();
    }

    public void registerHooks(EntityProcessor adder, EntityProcessor remover) {
	addHooks.add(adder);
	removeHooks.add(remover);
    }

    public void unregisterHooks(EntityProcessor adder, EntityProcessor remover) {
	addHooks.remove(adder);
	removeHooks.remove(remover);
    }
    
    public void addComponent(int eid, Object comp) {
	Class<?> compClass = comp.getClass();
	if(!active.get(eid)) {
	    return;
	}
	if(!componentArrays.containsKey(compClass)) {
	    componentArrays.put(compClass, new Object[MAX_ENTITIES]);
	}
	(componentArrays.get(compClass))[eid] = comp;
	for(EntityProcessor h : addHooks) {
	    h.process(eid);
	}
    }

    public void processEntities(EntityProcessor p) {
	for(int i=0;i<=maxEid;i++) {
	    if(active.get(i)) {
		p.process(i);
	    }
	}
    }
    
    public void processEntitiesWithComponent(Class<?> aClass, EntityProcessor p) {
	if(componentArrays.get(aClass) == null) {
	    return;
	}
	for(int i=0;i<=maxEid;i++) {
	    if(active.get(i) && (componentArrays.get(aClass) != null) &&
				((componentArrays.get(aClass))[i] != null)) {
		p.process(i);
	    }
	}
    }

    public int findEntityWithComponent(Class<?> aClass) {
	for(int j=0;j<=maxEid;j++) {
	    Object[] ca = componentArrays.get(aClass);
	    if(active.get(j) && (ca != null) && (ca[j] != null)) return j;
	}
	return NO_ENTITY;
    }
}
