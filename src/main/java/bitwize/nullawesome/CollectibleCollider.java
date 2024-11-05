package bitwize.nullawesome;

import java.util.Random;
import android.graphics.RectF;

public class CollectibleCollider implements CollisionUpdateAgent.Collider {
    private ContentRepository content = ContentRepository.get();
    private Random random = new Random();
    private SpriteShape sparkleShape = SpriteShape.loadAnimation(content.getAnimation("sparkles_anim"));

    RectF hitboxO = new RectF();
    RectF hitboxD = new RectF();
    public void collide(int eid1, int eid2) {
        int textEid;
        EntityRepository repo = EntityRepository.get();
        SpriteMovement movP, movC;
        WorldPhysics physP, physC;
        CollectibleInfo ciC;
        SpriteShape shpC;
        TextInfo ti = null;
        ciC = (CollectibleInfo)repo.getComponent(eid1, CollectibleInfo.class);
        movP = (SpriteMovement)repo.getComponent(eid2, SpriteMovement.class);
        movC = (SpriteMovement)repo.getComponent(eid1, SpriteMovement.class);
        physP = (WorldPhysics)repo.getComponent(eid2, WorldPhysics.class);
        physC = (WorldPhysics)repo.getComponent(eid1, WorldPhysics.class);
        shpC = (SpriteShape)repo.getComponent(eid1, SpriteShape.class);
        textEid = repo.findEntityWithComponent(TextInfo.class);
        if(textEid != EntityRepository.NO_ENTITY) {
            ti = (TextInfo)repo.getComponent(textEid, TextInfo.class);
        }
        if(ciC.state == CollectibleState.STATIONARY) {
            ciC.state = CollectibleState.VANISHING;
            SpriteShape.copyShape(sparkleShape, shpC);
            if((ciC.type == CollectibleType.INTEL) &&
               (ti != null)) {
                String s = String.format(TextInfo.copiedString,
                                         random.nextInt(990) + 10);
                ti.textBuffer.append(TextInfo.copyString);
                ti.textBuffer.append(s);
                ti.textBuffer.append(TextInfo.readyString);
                ti.showDisplay = true;
                ti.displayTime = ti.maxDisplayTime;
            }
        }
    }
}
