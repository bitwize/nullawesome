package bitwize.nullawesome;

import android.util.Log;
import android.graphics.PointF;
import android.graphics.Rect;
import org.json.*;
import java.util.HashMap;

public class ThingFactory {

    public static final int NO_THING=-1;
    
    private static HashMap<String, ThingType> namedTypes = new HashMap<String, ThingType>();
    static {
	for(ThingType t : ThingType.values()) {
	    namedTypes.put(t.toString().toLowerCase(), t);
	}
    }

    private static HashMap<String, EnemyType> namedEnemyTypes = new HashMap<String, EnemyType>();
    static {
	for(EnemyType t : EnemyType.values()) {
	    namedEnemyTypes.put(t.toString().toLowerCase(), t);
	}
    }

    private static HashMap<String, CollectibleType> namedCollectibleTypes = new HashMap<String, CollectibleType>();
    static {
	for(CollectibleType t : CollectibleType.values()) {
	    namedCollectibleTypes.put(t.toString().toLowerCase(), t);
	}
    }

    private static HashMap<ThingType, EntityProcessor> linkedThingTriggers;
    private static HashMap<ThingType, EntityProcessor> linkedThingResets;

    private static EntityProcessor elevatorAction = (eid) -> {
	EntityRepository repo = EntityRepository.get();
	ElevatorStates es = (ElevatorStates)repo.getComponent(eid, ElevatorStates.class);
	es.isAlternate = true;
	es.transitioning = true;
    };

    private static EntityProcessor elevatorReset = (eid) -> {
	EntityRepository repo = EntityRepository.get();
	ElevatorStates es = (ElevatorStates)repo.getComponent(eid, ElevatorStates.class);
	es.isAlternate = false;
	es.transitioning = true;
    };


    private static EntityProcessor doorAction = (eid) -> {
	EntityRepository repo = EntityRepository.get();
	DoorInfo di = (DoorInfo)repo.getComponent(eid, DoorInfo.class);
	di.state = DoorState.OPENING;
    };

    private static EntityProcessor doorReset = (eid) -> {
	EntityRepository repo = EntityRepository.get();
	DoorInfo di = (DoorInfo)repo.getComponent(eid, DoorInfo.class);
	di.state = DoorState.CLOSING;
    };

    private static DoorCollider dc = new DoorCollider();
    private static CollectibleCollider cc = new CollectibleCollider();
    
    static {
	linkedThingTriggers = new HashMap<ThingType, EntityProcessor>();
	linkedThingResets = new HashMap<ThingType, EntityProcessor>();
	linkedThingTriggers.put(ThingType.ELEVATOR, elevatorAction);
	linkedThingResets.put(ThingType.ELEVATOR, elevatorReset);
	linkedThingTriggers.put(ThingType.DOOR_SLIDE, doorAction);
	linkedThingResets.put(ThingType.DOOR_SLIDE, doorReset);
    }
    
    
    private static EntityProcessor terminalNodeAction = (eid) -> {
	EntityRepository repo = EntityRepository.get();
	HackTarget ht = (HackTarget)repo.getComponent(eid, HackTarget.class);
	SpriteOverlay ovl = (SpriteOverlay)repo.getComponent(eid, SpriteOverlay.class);
	TimerAction ta = (TimerAction)repo.getComponent(eid, TimerAction.class);
	WorldPhysics phys = (WorldPhysics)repo.getComponent(eid, WorldPhysics.class);
	StageInfo info = (StageInfo)repo.getComponent(phys.stageEid, StageInfo.class);
	ovl.draw.set(0, true);
	if(ta != null) {
	    ta.active = true;
	}
	int linkedEid = StageInfo.getEidForThing(info, ht.linkedThingIndex);
	if(linkedEid == EntityRepository.NO_ENTITY) return;
	ThingType linkedThingType = info.thingTypes[ht.linkedThingIndex];
	if(linkedThingTriggers.containsKey(linkedThingType)) {
		EntityProcessor action = linkedThingTriggers.get(linkedThingType);
		action.process(linkedEid);
	}
    };

    private static EntityProcessor terminalNodeReset = (eid) -> {
	EntityRepository repo = EntityRepository.get();
	HackTarget ht = (HackTarget)repo.getComponent(eid, HackTarget.class);
	SpriteOverlay ovl = (SpriteOverlay)repo.getComponent(eid, SpriteOverlay.class);
	WorldPhysics phys = (WorldPhysics)repo.getComponent(eid, WorldPhysics.class);
	StageInfo info = (StageInfo)repo.getComponent(phys.stageEid, StageInfo.class);
	if(ovl != null) ovl.draw.set(0, false);
	if(ht == null) return;
	ht.hacked = false;
	int linkedEid = StageInfo.getEidForThing(info, ht.linkedThingIndex);
	if(linkedEid == EntityRepository.NO_ENTITY) return;
	ThingType linkedThingType = info.thingTypes[ht.linkedThingIndex];
	if(linkedEid == EntityRepository.NO_ENTITY) return;
	if(linkedThingTriggers.containsKey(linkedThingType)) {
		EntityProcessor action = linkedThingResets.get(linkedThingType);
		action.process(linkedEid);
	}
    };

    private static int currentBobFrame = 0;
    
    public static int createTerminalNode(int stageEid, PointF location, int link)
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
	ht.linkedThingIndex = link;
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

    public static int createResettingTerminalNode(int stageEid, PointF location, int link, int resetTime)
	throws EntityTableFullException {
	EntityRepository repo = EntityRepository.get();
	int nodeEid = createTerminalNode(stageEid, location, link);
	TimerAction ta = new TimerAction();
	ta.nTicks = resetTime;
	ta.maxTicks = resetTime;
	ta.action = terminalNodeReset;
	repo.addComponent(nodeEid, ta);
	return nodeEid;
    }

    public static int createElevator(int stageEid, ElevatorState.Type type, PointF fulcrum, PointF start, float springc,
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

    public static int createSentryDrone(int stageEid, PointF location)
	throws EntityTableFullException
    {
	EntityRepository repo = EntityRepository.get();
	int eid = repo.newEntity();
	SpriteShape shp = SpriteShape.loadAnimation(ContentRepository.get().getAnimation("sentry_drone_stand"));
	shp.subsection = new Rect(0, 0, 32, 32);
	SpriteMovement mv = new SpriteMovement();
	WorldPhysics phys = new WorldPhysics();
	EnemyInfo ei = new EnemyInfo();
	EnemyCollider ec = new EnemyCollider();
	phys.stageEid = stageEid;
	phys.state = WorldPhysics.State.FALLING;
	phys.radius = 16;
	phys.hitbox.left = -16.f;
	phys.hitbox.top = -16.f;
	phys.hitbox.right = 16.f;
	phys.hitbox.bottom = 16.f;
	phys.flags |= WorldPhysics.SOLID_COLLISION;
	phys.collider = ec;
	phys.collisionCriterion = (cEid) -> EntityRepository.get()
	    .getComponent(cEid, PlayerInfo.class) != null;
	mv.position.set(location);
	mv.hotspot.set(16.f, 16.f);
	ei.type = EnemyType.SENTRY_DRONE;
	ei.currentState = EnemyState.IDLE;
	ei.targetEid = EntityRepository.NO_ENTITY;
	ei.flags = 0;
	ei.walkVel = 1.6f;
	ei.chaseVel = 3.2f;
	ei.sightRange = 128;
	ei.sightFrustum = (float)Math.atan(1);
	ei.stateActions.put(EnemyState.IDLE,
			    EnemyBehaviors.groundPatrol);
	ei.stateActions.put(EnemyState.ATTACKING,
				EnemyBehaviors.chase);
	ei.scriptSet(EnemyState.IDLE,
		     new EnemyStateTransition(EnemyBehaviors.seesTargetCriterion, EnemyState.ATTACKING));
	ei.scriptSet(EnemyState.ATTACKING,
		     new EnemyStateTransition(EnemyBehaviors.doesntSeeTargetCriterion, EnemyState.IDLE));
	ei.fireCooldown = 192;
	ei.fireRange = 160;
	repo.addComponent(eid, shp);
	repo.addComponent(eid, mv);
	repo.addComponent(eid, phys);
	repo.addComponent(eid, ei);
	return eid;
    }

	public static int createSoldier(int stageEid, PointF location)
			throws EntityTableFullException
	{
		EntityRepository repo = EntityRepository.get();
		int eid = repo.newEntity();
		SpriteShape shp = SpriteShape.loadAnimation(ContentRepository.get().getAnimation("soldier_stand"));
		SpriteMovement mv = new SpriteMovement();
		WorldPhysics phys = new WorldPhysics();
		EnemyInfo ei = new EnemyInfo();
		EnemyCollider ec = new EnemyCollider();
		phys.stageEid = stageEid;
		phys.state = WorldPhysics.State.FALLING;
		phys.radius = 16;
		phys.hitbox.left = -16.f;
		phys.hitbox.top = -16.f;
		phys.hitbox.right = 16.f;
		phys.hitbox.bottom = 16.f;
		phys.flags |= WorldPhysics.SOLID_COLLISION;
		phys.collider = ec;
		phys.collisionCriterion = (cEid) -> EntityRepository.get()
				.getComponent(cEid, PlayerInfo.class) != null;
		mv.position.set(location);
		mv.hotspot.set(16.f, 16.f);
		ei.type = EnemyType.SOLDIER;
		ei.currentState = EnemyState.IDLE;
		ei.targetEid = EntityRepository.NO_ENTITY;
		ei.flags = 0;
		ei.walkVel = 0.9f;
		ei.chaseVel = 1.8f;
		ei.sightRange = 128;
		ei.sightFrustum = (float)Math.atan(1);
		ei.stateActions.put(EnemyState.IDLE,
				EnemyBehaviors.groundPatrol);
		ei.stateActions.put(EnemyState.ATTACKING,
				EnemyBehaviors.chase);
		ei.scriptSet(EnemyState.IDLE,
				new EnemyStateTransition(EnemyBehaviors.seesTargetCriterion, EnemyState.ATTACKING));
		ei.scriptSet(EnemyState.ATTACKING,
				new EnemyStateTransition(EnemyBehaviors.doesntSeeTargetCriterion, EnemyState.IDLE));
		ei.fireCooldown = 12;
		ei.fireRange = 160;
		repo.addComponent(eid, shp);
		repo.addComponent(eid, mv);
		repo.addComponent(eid, phys);
		repo.addComponent(eid, ei);
		return eid;
	}

    public static int createEnemy(int stageEid, EnemyType etype, PointF location)
	throws EntityTableFullException
    {
	switch(etype) {
	case SENTRY_DRONE:
	    return createSentryDrone(stageEid, location);
	case SOLDIER:
		return createSoldier(stageEid, location);
	default:
	    return EntityRepository.NO_ENTITY;
	}
    }

    public static int createDoor(int stageEid, PointF location)
	throws EntityTableFullException
    {
	EntityRepository repo = EntityRepository.get();
	int eid = repo.newEntity();
	SpriteShape shp = new SpriteShape();
	SpriteMovement mv = new SpriteMovement();
	WorldPhysics phys = new WorldPhysics();
	shp.shapes = ContentRepository.get().getBitmap("door");
	shp.subsection = new Rect(0, 0, DoorInfo.DOOR_WIDTH, DoorInfo.DOOR_HEIGHT);
	mv.position.set(location);
	mv.position.x = (float)(((int)mv.position.x / TileMap.TILE_SIZE) * TileMap.TILE_SIZE
				+ (TileMap.TILE_SIZE / 2));
	mv.position.y = (float)(((int)mv.position.y / TileMap.TILE_SIZE) * TileMap.TILE_SIZE);	
	mv.hotspot.set(DoorInfo.DOOR_WIDTH / 2, DoorInfo.DOOR_HEIGHT);
	phys.stageEid = stageEid;
	phys.state = WorldPhysics.State.GROUNDED;
	phys.gvelmax = 0.f;
	phys.radius = DoorInfo.DOOR_WIDTH / 2;
	phys.hitbox.left = -(DoorInfo.DOOR_WIDTH / 2);
	phys.hitbox.top = -(DoorInfo.DOOR_HEIGHT);
	phys.hitbox.right = DoorInfo.DOOR_WIDTH / 2;
	phys.hitbox.bottom = 0;
	phys.collider = dc;
	phys.collisionCriterion = (cEid) -> EntityRepository.get()
	    .getComponent(cEid, WorldPhysics.class) != null;
	DoorInfo di = new DoorInfo();
	repo.addComponent(eid, shp);
	repo.addComponent(eid, di);
	repo.addComponent(eid, mv);
	repo.addComponent(eid, phys);
	return eid;
    }

    public static int createCollectible(int stageEid, CollectibleType type, PointF location)
    	throws EntityTableFullException
    {
	EntityRepository repo = EntityRepository.get();
	int eid = repo.newEntity();
	SpriteShape shp = new SpriteShape();
	SpriteMovement mv = new SpriteMovement();
	WorldPhysics phys = new WorldPhysics();
	CollectibleInfo ci = new CollectibleInfo();
	shp.shapes = ContentRepository.get().getBitmap("collectibles");
	shp.subsection = new Rect(0, 0, CollectibleInfo.WIDTH, CollectibleInfo.HEIGHT);
	mv.position.set(location);
	for(int i=0;i<ThingFactory.currentBobFrame;i++) {
	    mv.position.y += CollectibleUpdateAgent.bobbingDisplacements[i];
	}
	mv.hotspot.set( CollectibleInfo.WIDTH / 2, CollectibleInfo.HEIGHT / 2);
	phys.stageEid = stageEid;
	phys.state = WorldPhysics.State.FALLING;
	phys.gvelmax = 0.f;
	phys.gravity.set(0.f, 0.f);
	phys.radius = CollectibleInfo.WIDTH / 2;
	phys.hitbox.left = -(CollectibleInfo.WIDTH / 2);
	phys.hitbox.top = -(CollectibleInfo.HEIGHT / 2);
	phys.hitbox.right = CollectibleInfo.WIDTH / 2;
	phys.hitbox.bottom = CollectibleInfo.HEIGHT / 2;
	phys.collider = cc;
	phys.collisionCriterion = (cEid) -> EntityRepository.get()
	    .getComponent(cEid, PlayerInfo.class) != null;
	ci.type = type;
	ci.state = CollectibleState.STATIONARY;
	ci.bobbingFrame = ThingFactory.currentBobFrame;
	ThingFactory.currentBobFrame++;
	ThingFactory.currentBobFrame &= 7;
	repo.addComponent(eid, shp);
	repo.addComponent(eid, ci);
	repo.addComponent(eid, mv);
	repo.addComponent(eid, phys);
	return eid;
    }
    
    private static int createThing(int stageEid,
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
	ThingType type = namedTypes.get(obj.getString("type"));
	if(type == null) {
	    throw new RuntimeException("invalid thing type");
	}
	PointF loc = new PointF(x, y);
	switch(type) {
	case TERMINAL_NODE:
	    {
		int link = obj.has("link") ? obj.getInt("link") : -1;
		if(obj.has("reset_time")) {
		    int resetTime = obj.getInt("reset_time");
		    return createResettingTerminalNode(stageEid, loc, link, resetTime);
		} else {
		    return createTerminalNode(stageEid, loc, link);
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
	case ENEMY:
	    {
		EnemyType etype = namedEnemyTypes.get(obj.getString("enemy_type"));
		return createEnemy(stageEid, etype, loc);
	    }
	case DOOR_SLIDE:
	    {
		return createDoor(stageEid, loc);
	    }
	case COLLECTIBLE:
	    {
		CollectibleType ctype = namedCollectibleTypes.get(obj.getString("collectible_type"));
		return createCollectible(stageEid, ctype, loc);
	    }
	default:
	    return EntityRepository.NO_ENTITY;
	}
	
    }
    public static void createThings(int stageEid)
    	throws EntityTableFullException, JSONException {
	EntityRepository repo = EntityRepository.get();
	StageInfo info = (StageInfo)repo.getComponent(stageEid, StageInfo.class);
	if(info == null) return;
	for(int i=0; i < info.thingIds.length; i++) {
	    info.thingIds[i] = createThing(stageEid, i);
	    info.thingTypes[i] = namedTypes.get(info.thingParams[i].getString("type"));
	}
    }
    
}
