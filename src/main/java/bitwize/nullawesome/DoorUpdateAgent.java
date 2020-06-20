package bitwize.nullawesome;

public class DoorUpdateAgent implements UpdateAgent {
    private EntityRepository repo;
    private RelevantEntitiesHolder reh = new RelevantEntitiesHolder(RelevantEntitiesHolder.hasComponentCriterion(DoorInfo.class));
    private EntityProcessor proc = (eid) -> {
	DoorInfo di = (DoorInfo)repo.getComponent(eid, DoorInfo.class);
	SpriteShape shp = (SpriteShape)repo.getComponent(eid, SpriteShape.class);
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
}
