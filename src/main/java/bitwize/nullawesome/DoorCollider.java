package bitwize.nullawesome;

import android.graphics.RectF;

public class DoorCollider implements CollisionUpdateAgent.Collider {
    RectF hitboxO = new RectF();
    RectF hitboxD = new RectF();
    public void collide(int eid1, int eid2) {
	EntityRepository repo = EntityRepository.get();
	SpriteMovement movO, movD;
	WorldPhysics physO, physD;
	DoorInfo diD;
	movO = (SpriteMovement)repo.getComponent(eid2, SpriteMovement.class);
	movD = (SpriteMovement)repo.getComponent(eid1, SpriteMovement.class);
	physO = (WorldPhysics)repo.getComponent(eid2, WorldPhysics.class);
	physD = (WorldPhysics)repo.getComponent(eid1, WorldPhysics.class);
	diD = (DoorInfo)repo.getComponent(eid1, DoorInfo.class);
	if(movO == null || physO == null || movD == null || physD == null) { return; }
	if((physO.flags & WorldPhysics.SOLID_COLLISION) == 0) { return; }
	if(diD.open >= 8) return;
	hitboxD.set(physD.hitbox);
	hitboxD.offset(movD.position.x, movD.position.y);
	if(movO.position.x < movD.position.x) {
	    movO.position.x = movD.position.x + physD.hitbox.left - physO.hitbox.right;
	} else {
	    movO.position.x = movD.position.x - physO.hitbox.left + physD.hitbox.right;
	}
    }
}
