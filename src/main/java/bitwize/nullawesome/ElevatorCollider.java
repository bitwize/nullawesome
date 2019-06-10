package bitwize.nullawesome;

import android.graphics.RectF;

public class ElevatorCollider implements CollisionUpdateAgent.Collider {
    RectF hitboxO = new RectF();
    RectF hitboxE = new RectF();
    public void collide(int eid1, int eid2) {
	EntityRepository repo = EntityRepository.get();
	SpriteMovement movO, movE;
	WorldPhysics physO, physE;
	movO = (SpriteMovement)repo.getComponent(eid1, SpriteMovement.class);
	movE = (SpriteMovement)repo.getComponent(eid2, SpriteMovement.class);
	physO = (WorldPhysics)repo.getComponent(eid1, WorldPhysics.class);
	physE = (WorldPhysics)repo.getComponent(eid2, WorldPhysics.class);
	if(movO == null || physO == null || movE == null || physE == null) { return; }
	if((physO.flags & WorldPhysics.SOLID_COLLISION) == 0) { return; }
	hitboxO.set(physO.hitbox);
	hitboxO.offset(movO.position.x, movO.position.y);
	hitboxE.set(physE.hitbox);
	hitboxE.offset(movE.position.x, movE.position.y);
	if((movO.velocity.y > 0) &&
	   (hitboxO.bottom < hitboxE.bottom)) {
	    physO.state = WorldPhysics.State.GROUNDED;
	    movO.velocity.y = 0.f;
	    movO.acceleration.y = 0.f;
	    physO.thrust.set(0.f, 0.f);
	    physO.sticksToEid = eid2;
	    physO.sticksToPosition.set(movE.position);
	    movO.position.y = hitboxE.top + 1 - physO.hitbox.bottom;
	}
    }
}
