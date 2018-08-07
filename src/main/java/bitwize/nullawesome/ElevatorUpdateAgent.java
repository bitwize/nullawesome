package bitwize.nullawesome;

import android.graphics.PointF;

public class ElevatorUpdateAgent implements UpdateAgent {
    private EntityRepository repo;
    private EntityProcessor proc = new EntityProcessor() {
	    public void process(int eid) {
		ElevatorState es = (ElevatorState)repo.getComponent(eid, ElevatorState.class);
		SpriteMovement mv = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
		WorldPhysics phys = (WorldPhysics)repo.getComponent(eid, WorldPhysics.class);
		if((es == null) || (mv == null) || (phys == null)) return;
		float xdisp = Math.abs(es.fulcrum.x - es.startPoint.x);
		float ydisp = Math.abs(es.fulcrum.y - es.startPoint.y);
		float coef = Math.abs(es.springConstant);
		mv.position.x = (mv.position.x < es.fulcrum.x - xdisp
				 ? es.fulcrum.x - xdisp
				 : (mv.position.x > es.fulcrum.x + xdisp
				    ? es.fulcrum.x + xdisp
				    : mv.position.x));
		mv.position.y = (mv.position.y < es.fulcrum.y - ydisp
				 ? es.fulcrum.y - ydisp
				 : (mv.position.y > es.fulcrum.y + ydisp
				    ? es.fulcrum.y + ydisp
				    : mv.position.y));
		phys.thrust.set((es.fulcrum.x - mv.position.x) * coef,
				    (es.fulcrum.y - mv.position.y) * coef);
	    }
	};
    public ElevatorUpdateAgent() {
	repo = EntityRepository.get();
    }
    public void update(long time) {
	repo.processEntitiesWithComponent(ElevatorState.class, proc);
    }
}
