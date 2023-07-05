package bitwize.nullawesome;

import android.graphics.RectF;

public class EnemyCollider implements CollisionUpdateAgent.Collider {

    public void collide(int eid1, int eid2) {
        EntityRepository repo = EntityRepository.get();
        SpriteMovement movP, movE;
        WorldPhysics physP, physE;
        movP = (SpriteMovement)repo.getComponent(eid2, SpriteMovement.class);
        movE = (SpriteMovement)repo.getComponent(eid1, SpriteMovement.class);
        physP = (WorldPhysics)repo.getComponent(eid2, WorldPhysics.class);
        physE = (WorldPhysics)repo.getComponent(eid1, WorldPhysics.class);
        physP.flags |= WorldPhysics.SHOULD_DESTROY;
    }
}
