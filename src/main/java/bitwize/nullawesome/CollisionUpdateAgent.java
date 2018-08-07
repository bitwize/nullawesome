package bitwize.nullawesome;

import android.graphics.RectF;

public class CollisionUpdateAgent implements UpdateAgent {
    private EntityRepository repo;
    private RectF r1, r2;
    private int currentEid = EntityRepository.NO_ENTITY;
    private EntityProcessor collProc = new EntityProcessor() {
	    public void process(int eid) {
		if(eid == currentEid) return;
		WorldPhysics wp1 = (WorldPhysics)repo.getComponent(currentEid, WorldPhysics.class);
		if(wp1 == null) return;
		if(!wp1.collisionCriterion.test(eid)) return;
		SpriteMovement mov1 = (SpriteMovement)repo.getComponent(currentEid, SpriteMovement.class);
		SpriteMovement mov2 = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
		WorldPhysics wp2 = (WorldPhysics)repo.getComponent(eid, WorldPhysics.class);
		if(mov1 == null || mov2 == null
		   || wp2 == null) return;
		r1.set(wp1.hitbox);
		r2.set(wp2.hitbox);
		r1.offset(mov1.position.x, mov1.position.y);
		r2.offset(mov2.position.x, mov2.position.y);
		if(RectF.intersects(r1, r2)) {
		    wp1.collider.collide(currentEid, eid);
		}
	    }
	};

    private EntityProcessor proc = new EntityProcessor() {
	    public void process(int eid) {
		currentEid = eid;
		repo.processEntitiesWithComponent(WorldPhysics.class, collProc);
	    }
	};
    
    public interface Collider {
	public void collide(int eid1, int eid2);
    }

    public interface Criterion {
	public boolean test(int eid);
    }

    public static final Criterion nullCriterion = new Criterion() {
	    public boolean test(int eid) {
		return false;
	    }
	};

    public static final Criterion allCriterion = new Criterion() {
	    public boolean test(int eid) {
		return true;
	    }
	};

    public static final Collider nullCollider = new Collider() {
	    public void collide(int eid1, int eid2) {

	    }
	};
    
    public CollisionUpdateAgent() {
	r1 = new RectF();
	r2 = new RectF();
	repo = EntityRepository.get();
    }

    public void update(long time) {
	repo.processEntitiesWithComponent(WorldPhysics.class, proc);
    }
}
