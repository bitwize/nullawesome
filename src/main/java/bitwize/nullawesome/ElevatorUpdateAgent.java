package bitwize.nullawesome;

import android.graphics.PointF;
import android.util.Log;

public class ElevatorUpdateAgent implements UpdateAgent {
    private EntityRepository repo;
    private RelevantEntitiesHolder reh = new RelevantEntitiesHolder(RelevantEntitiesHolder.hasComponentCriterion(ElevatorStates.class));
    private EntityProcessor proc = (eid) -> {
	ElevatorState targetState, sourceState;
	ElevatorStates es = (ElevatorStates)repo.getComponent(eid, ElevatorStates.class);
	SpriteMovement mv = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
	WorldPhysics phys = (WorldPhysics)repo.getComponent(eid, WorldPhysics.class);
	if((es == null) || (mv == null) || (phys == null)) return;
	targetState = es.isAlternate ? es.alternateState : es.primaryState;
	sourceState = es.isAlternate ? es.primaryState : es.alternateState;
	float xdisp = Math.abs(targetState.fulcrum.x - targetState.startPoint.x);
	float ydisp = Math.abs(targetState.fulcrum.y - targetState.startPoint.y);
	float coef = Math.abs(targetState.springConstant);
	if(es.transitioning) {
	    phys.thrust.set(0.f, 0.f);
	    float dx = sourceState.startPoint.x - mv.position.x;
	    float dy = sourceState.startPoint.y - mv.position.y;
	    float dist = (float)Math.sqrt((dx * dx) + (dy * dy));
	    float distrat;
	    // once we've arrived, stop transitioning
	    if(dist < es.transitionSpeed * 2) {
		es.transitioning = false;
		mv.position.x = sourceState.startPoint.x;
		mv.position.y = sourceState.startPoint.y;
		mv.velocity.x = 0.f;
		mv.velocity.y = 0.f;
		mv.acceleration.x = 0.f;
		mv.acceleration.y = 0.f;
		return;
	    }
	    distrat = es.transitionSpeed / dist;
	    mv.velocity.x = dx * distrat;
	    mv.velocity.y = dy * distrat;
	    mv.acceleration.x = 0.f;
	    mv.acceleration.y = 0.f;
	    return;
	}
	switch(targetState.type) {
	case OSCILLATING:
	mv.position.x = (mv.position.x < targetState.fulcrum.x - xdisp
			 ? targetState.fulcrum.x - xdisp
			 : (mv.position.x > targetState.fulcrum.x + xdisp
			    ? targetState.fulcrum.x + xdisp
			    : mv.position.x));
	mv.position.y = (mv.position.y < targetState.fulcrum.y - ydisp
			 ? targetState.fulcrum.y - ydisp
			 : (mv.position.y > targetState.fulcrum.y + ydisp
			    ? targetState.fulcrum.y + ydisp
			    : mv.position.y));
	phys.thrust.set((targetState.fulcrum.x - mv.position.x) * coef,
			(targetState.fulcrum.y - mv.position.y) * coef);
	break;
	case STATIONARY:
	mv.position.set(targetState.startPoint.x, targetState.startPoint.y);
	mv.velocity.set(0.f, 0.f);
	phys.thrust.set(0.f, 0.f);
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
