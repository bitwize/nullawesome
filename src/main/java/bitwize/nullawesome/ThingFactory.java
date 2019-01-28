package bitwize.nullawesome;

import android.graphics.PointF;
import android.graphics.Rect;
import org.json.*;
import java.util.HashMap;

public class ThingFactory {

    public static final int NO_THING=-1;
    
    public enum ThingType {
	TERMINAL_NODE,
	DOOR_SLIDE,
	ELEVATOR
    }
    private static HashMap<String, ThingType> namedTypes = new HashMap<String, ThingType>();
    static {
	for(ThingType t : ThingType.values()) {
	    namedTypes.put(t.toString().toLowerCase(), t);
	}
    }

    private static EntityProcessor terminalNodeAction = new EntityProcessor() {
	    public void process(int eid) {
		EntityRepository repo = EntityRepository.get();
		SpriteOverlay ovl = (SpriteOverlay)repo.getComponent(eid, SpriteOverlay.class);
		TimerAction ta = (TimerAction)repo.getComponent(eid, TimerAction.class);
		ovl.draw.set(0, true);
		if(ta != null) {
		    ta.active = true;
		}
	    }
	};

    private static EntityProcessor terminalNodeReset = new EntityProcessor() {
	    public void process(int eid) {
		EntityRepository repo = EntityRepository.get();
		HackTarget ht = (HackTarget)repo.getComponent(eid, HackTarget.class);
		SpriteOverlay ovl = (SpriteOverlay)repo.getComponent(eid, SpriteOverlay.class);
		if(ht != null) ht.hacked = false;
		if(ovl != null) ovl.draw.set(0, false);
	    }
	};
    
    public int createTerminalNode(int stageEid, PointF location)
	throws EntityTableFullException {
	EntityRepository repo = EntityRepository.get();
	int eid = repo.newEntity();
	SpriteShape shp = new SpriteShape();
	shp.shapes = ContentRepository.get().getBitmap("terminal");
	SpriteShape shp2 = new SpriteShape();
	shp2.shapes = ContentRepository.get().getBitmap("terminal_greentext");
	shp.subsection = new Rect(0, 0, 19, 32);
	shp2.subsection = new Rect(0, 0, 9, 7);
	SpriteMovement mv = new SpriteMovement();
	WorldPhysics phys = new WorldPhysics();
	SpriteOverlay ovl = new SpriteOverlay(1)
	    .put(0, shp2, -1.f, -14.f);
	ovl.draw.set(0, false);
	mv.position.set(location.x, location.y);
	mv.hotspot.set(10, 16);
	HackTarget ht = new HackTarget();
	ht.width = 48;
	ht.height = 48;
	ht.action = terminalNodeAction;
	phys.stageEid = stageEid;
	phys.state = WorldPhysics.State.GROUNDED;
	phys.gvelmax = 2.f;
	phys.radius = 16;
	phys.hitbox.left = -10.f;
	phys.hitbox.top = -16.f;
	phys.hitbox.right = 9.f;
	phys.hitbox.bottom = 16.f;
	repo.addComponent(eid, shp);
	repo.addComponent(eid, mv);
	repo.addComponent(eid, ht);
	repo.addComponent(eid, phys);
	repo.addComponent(eid, ovl);
	return eid;
    }

    public int createResettingTerminalNode(int stageEid, PointF location, int resetTime)
	throws EntityTableFullException {
	EntityRepository repo = EntityRepository.get();
	int nodeEid = createTerminalNode(stageEid, location);
	TimerAction ta = new TimerAction();
	ta.nTicks = resetTime;
	ta.maxTicks = resetTime;
	ta.action = terminalNodeReset;
	repo.addComponent(nodeEid, ta);
	return nodeEid;
    }

    public int createElevator(int stageEid, ElevatorState.Type type, PointF fulcrum, PointF start, float springc,
			      ElevatorState.Type altType, PointF altFulcrum, PointF altStart, float altSpringc)
	throws EntityTableFullException {
	EntityRepository repo = EntityRepository.get();
	int eid = repo.newEntity();
	SpriteShape shp = new SpriteShape();
	shp.shapes = ContentRepository.get().getBitmap("elevator1");
	shp.subsection = new Rect(0, 0, 64, 16);
	SpriteMovement mv = new SpriteMovement();
	WorldPhysics phys = new WorldPhysics();
	phys.stageEid = stageEid;
	phys.state = WorldPhysics.State.FALLING;
	phys.gvelmax = 999.f;
	phys.gravity.y = 0.f;
	phys.radius = 16;
	phys.hitbox.left = -32.f;
	phys.hitbox.top = -8.f;
	phys.hitbox.right = 32.f;
	phys.hitbox.bottom = 8.f;
	ElevatorStates est = new ElevatorStates();
	est.primaryState.type = type;
	est.primaryState.fulcrum.set(fulcrum);
	est.primaryState.startPoint.set(start);
	mv.position.set(start);
	mv.hotspot.set(32.f, 8.f);
	est.primaryState.springConstant = springc;
	est.alternateState.type = altType;
	est.alternateState.fulcrum.set(altFulcrum);
	est.alternateState.startPoint.set(altStart);
	est.alternateState.springConstant = altSpringc;
	repo.addComponent(eid, shp);
	repo.addComponent(eid, mv);
	repo.addComponent(eid, phys);
	repo.addComponent(eid, est);
	return eid;
    }

    private int createThing(int stageEid,
			    int thingIndex)
	throws EntityTableFullException, JSONException {
	// TODO: dispatch on thing type and populate the entity with
	// appropriate components
	EntityRepository repo = EntityRepository.get();
	StageInfo info = (StageInfo)repo.getComponent(stageEid, StageInfo.class);
	if(info == null) return -1;
	JSONObject obj = info.thingParams[thingIndex];
	int x = obj.getInt("x");
	int y = obj.getInt("y");
	String type = obj.getString("type");
	if(namedTypes.get(type) == null) {
	    throw new RuntimeException("invalid thing type");
	}
	PointF loc = new PointF(x, y);
	switch(namedTypes.get(type)) {
	case TERMINAL_NODE:
	    {
		if(obj.has("reset_time")) {
		    int resetTime = obj.getInt("reset_time");
		    return createResettingTerminalNode(stageEid, loc, resetTime);
		} else {
		    return createTerminalNode(stageEid, loc);
		}
	    }
	case ELEVATOR:
	    if(obj.has("normal_state") && obj.has("alt_state")) {
		JSONObject normalState = obj.getJSONObject("normal_state");
		JSONObject alternateState = obj.getJSONObject("alt_state");
		return createElevator(stageEid,
				      ElevatorState.Type.byName(normalState.getString("type")),
				      new PointF((float)normalState.getInt("fulcrum_x"),
						 (float)normalState.getInt("fulcrum_y")),
				      new PointF((float)obj.getInt("x"),
						 (float)obj.getInt("y")),
				      (float)normalState.getDouble("spring_constant"),
				      ElevatorState.Type.byName(alternateState.getString("type")),
				      new PointF((float)alternateState.getInt("fulcrum_x"),
						 (float)alternateState.getInt("fulcrum_y")),
				      new PointF((float)alternateState.getInt("x"),
						 (float)alternateState.getInt("y")),
				      (float)alternateState.getDouble("spring_constant"));
	    } else {
		return -1;
	    }
	default:
	    return -1;
	}
	
    }
    public void createThings(int stageEid)
    	throws EntityTableFullException, JSONException {
	EntityRepository repo = EntityRepository.get();
	StageInfo info = (StageInfo)repo.getComponent(stageEid, StageInfo.class);
	if(info == null) return;
	for(int i=0; i < info.thingIds.length; i++) {
	    info.thingIds[i] = createThing(stageEid, i);
	}
    }
    
}
