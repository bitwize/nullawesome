package bitwize.nullawesome;

public class DoorUpdateAgent implements UpdateAgent {
    private EntityRepository repo;
    private RelevantEntitiesHolder reh = new RelevantEntitiesHolder(RelevantEntitiesHolder.hasComponentCriterion(DoorInfo.class));
    private EntityProcessor proc = (eid) -> {
	DoorInfo di = (DoorInfo)repo.getComponent(eid, DoorInfo.class);
	SpriteShape shp = (SpriteShape)repo.getComponent(eid, SpriteShape.class);
	SpriteMovement mv = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
	WorldPhysics phys = (WorldPhysics)repo.getComponent(eid, WorldPhysics.class);
	if(shp == null ||
	   di == null ||
	   mv == null ||
	   phys == null) return;
	StageInfo info = (StageInfo)repo.getComponent(phys.stageEid, StageInfo.class);
	if(info == null) return;
	TileMap map = info.map;
	switch(di.state) {
	case OPENING:
	    di.open++;
	    if(di.open >= DoorInfo.MAX_OPEN) {
		di.open = DoorInfo.MAX_OPEN - 1;
		di.state = DoorState.OPEN;
	    }
	    break;
	case CLOSING:
	    di.open--;
	    if(di.open < 0) {
		di.open = 0;
		di.state = DoorState.CLOSED;
	    }
	    break;
	}
	if(di.state == DoorState.CLOSED) {
	    blockTiles(map, (int)mv.position.x / TileMap.TILE_SIZE, (int)mv.position.y / TileMap.TILE_SIZE);
	} else {
	    clearTiles(map, (int)mv.position.x / TileMap.TILE_SIZE, (int)mv.position.y / TileMap.TILE_SIZE);
	}
	if(shp != null) {
	    int ypos = di.open * DoorInfo.DOOR_HEIGHT;
	    shp.subsection.set(0, ypos, DoorInfo.DOOR_WIDTH, ypos + DoorInfo.DOOR_HEIGHT);
	}
    };
    public DoorUpdateAgent() {
	reh.register();
	repo = EntityRepository.get();
    }
    public void update(long time) {
	if(time % 3 == 0) {
	    reh.processAll(proc);
	}
    }
    public static void blockTiles(TileMap map, int x, int y) {
	map.setTile(x, y - 1, (short)1);
	map.setTile(x, y - 2, (short)1);
    }
    public static void clearTiles(TileMap map, int x, int y) {
	map.setTile(x, y - 1, (short)0);
	map.setTile(x, y - 2, (short)0);
    }
	    
}
