package bitwize.nullawesome;

import android.graphics.RectF;

public class CollectibleCollider implements CollisionUpdateAgent.Collider {
    private ContentRepository content = ContentRepository.get();
    private SpriteShape sparkleShape = SpriteShape.loadAnimation(content.getAnimation("sparkles_anim"));

    RectF hitboxO = new RectF();
    RectF hitboxD = new RectF();
    public void collide(int eid1, int eid2) {
	EntityRepository repo = EntityRepository.get();
	SpriteMovement movP, movC;
	WorldPhysics physP, physC;
	CollectibleInfo ciC;
	SpriteShape shpC;
	ciC = (CollectibleInfo)repo.getComponent(eid1, CollectibleInfo.class);
	movP = (SpriteMovement)repo.getComponent(eid2, SpriteMovement.class);
	movC = (SpriteMovement)repo.getComponent(eid1, SpriteMovement.class);
	physP = (WorldPhysics)repo.getComponent(eid2, WorldPhysics.class);
	physC = (WorldPhysics)repo.getComponent(eid1, WorldPhysics.class);
	shpC = (SpriteShape)repo.getComponent(eid1, SpriteShape.class);
	if(ciC.state == CollectibleState.STATIONARY) {
	    ciC.state = CollectibleState.VANISHING;
	    SpriteShape.copyShape(sparkleShape, shpC);
	}
    }
}
