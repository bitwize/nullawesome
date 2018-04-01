package bitwize.nullawesome;

import android.graphics.PointF;
import android.graphics.Rect;
import org.json.*;

public class ThingFactory {

    public enum ThingType {
	TERMINAL_NODE,
	DOOR_SLIDE,
	ELEV_LR,
	ELEV_UD
    }

    public ThingType[] allTypes = ThingType.values();

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
	phys.hitbox.left = -10;
	phys.hitbox.top = -16;
	phys.hitbox.right = 9;
	phys.hitbox.bottom = 16;
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
	int type = obj.getInt("type");
	PointF loc = new PointF(x, y);
	switch(allTypes[type]) {
	case TERMINAL_NODE:
	    {
		if(obj.has("reset_time")) {
		    int resetTime = obj.getInt("reset_time");
		    return createResettingTerminalNode(stageEid, loc, resetTime);
		} else {
		    return createTerminalNode(stageEid, loc);
		}
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
