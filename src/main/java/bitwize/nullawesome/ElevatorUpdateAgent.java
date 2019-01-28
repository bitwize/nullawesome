package bitwize.nullawesome;

import android.graphics.PointF;

public class ElevatorUpdateAgent implements UpdateAgent {
    private EntityRepository repo;
    private RelevantEntitiesHolder reh = new RelevantEntitiesHolder(RelevantEntitiesHolder.hasComponentCriterion(ElevatorStates.class));
    private EntityProcessor proc = (eid) -> {
	ElevatorStates es = (ElevatorStates)repo.getComponent(eid, ElevatorStates.class);
	SpriteMovement mv = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
	WorldPhysics phys = (WorldPhysics)repo.getComponent(eid, WorldPhysics.class);
	if((es == null) || (mv == null) || (phys == null)) return;
	float xdisp = Math.abs(es.primaryState.fulcrum.x - es.primaryState.startPoint.x);
	float ydisp = Math.abs(es.primaryState.fulcrum.y - es.primaryState.startPoint.y);
	float coef = Math.abs(es.primaryState.springConstant);
	if(es.transitioning) {
	    float dx = es.alternateState.startPoint.x - mv.position.x;
	    float dy = es.alternateState.startPoint.y - mv.position.y;
	    float dist = (float)Math.sqrt((dx * dx) + (dy * dy));
	    float distrat;
	    // once we've arrived, stop transitioning
	    if(dist < es.transitionSpeed * 2) {
		es.transitioning = false;
		mv.position.x = es.alternateState.startPoint.x;
		mv.position.y = es.alternateState.startPoint.y;
		ElevatorState estemp = es.alternateState;
		es.alternateState = es.primaryState;
		es.primaryState = estemp;
		return;
	    }
	    distrat = es.transitionSpeed / dist;
	    mv.velocity.x = dx * distrat;
	    mv.velocity.y = dy * distrat;
	}
	switch(es.primaryState.type) {
	case OSCILLATING:
	mv.position.x = (mv.position.x < es.primaryState.fulcrum.x - xdisp
			 ? es.primaryState.fulcrum.x - xdisp
			 : (mv.position.x > es.primaryState.fulcrum.x + xdisp
			    ? es.primaryState.fulcrum.x + xdisp
			    : mv.position.x));
	mv.position.y = (mv.position.y < es.primaryState.fulcrum.y - ydisp
			 ? es.primaryState.fulcrum.y - ydisp
			 : (mv.position.y > es.primaryState.fulcrum.y + ydisp
			    ? es.primaryState.fulcrum.y + ydisp
			    : mv.position.y));
	phys.thrust.set((es.primaryState.fulcrum.x - mv.position.x) * coef,
			(es.primaryState.fulcrum.y - mv.position.y) * coef);
	break;
	case STATIONARY:
	break;
	}
    };
    public ElevatorUpdateAgent() {
	reh.register();
	repo = EntityRepository.get();
    }
    public void update(long time) {
	reh.processAll(proc);
    }
}
