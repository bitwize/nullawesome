package bitwize.nullawesome;

import java.util.Random;
import android.graphics.RectF;

public class CollectibleCollider implements CollisionUpdateAgent.Collider {
    private ContentRepository content = ContentRepository.get();
    private Random random = new Random();
    private SpriteShape sparkleShape = SpriteShape.loadAnimation(content.getAnimation("sparkles_anim"));
    private int pickupSound = ContentRepository.get().getSoundID("pickup");
    RectF hitboxO = new RectF();
    RectF hitboxD = new RectF();
    public void collide(int eid1, int eid2) {
        int textEid;
        int stageEid;
        EntityRepository repo = EntityRepository.get();
        SpriteMovement movP = null, movC = null, movS = null;
        WorldPhysics physP, physC;
        CollectibleInfo ciC;
        SpriteShape shpC;
        TextInfo ti = null;
        SoundInfo sound = null;
        ciC = (CollectibleInfo)repo.getComponent(eid1, CollectibleInfo.class);
        movP = (SpriteMovement)repo.getComponent(eid2, SpriteMovement.class);
        movC = (SpriteMovement)repo.getComponent(eid1, SpriteMovement.class);
        physP = (WorldPhysics)repo.getComponent(eid2, WorldPhysics.class);
        physC = (WorldPhysics)repo.getComponent(eid1, WorldPhysics.class);
        shpC = (SpriteShape)repo.getComponent(eid1, SpriteShape.class);
        textEid = repo.findEntityWithComponent(TextInfo.class);
        stageEid = repo.findEntityWithComponent(SoundInfo.class);
        if(textEid != EntityRepository.NO_ENTITY) {
            ti = (TextInfo)repo.getComponent(textEid, TextInfo.class);
        }
        if(stageEid != EntityRepository.NO_ENTITY) {
            sound = (SoundInfo)repo.getComponent(stageEid, SoundInfo.class);
            movS = (SpriteMovement)repo.getComponent(stageEid, SpriteMovement.class);
        }
        if(ciC.state == CollectibleState.STATIONARY) {
            ciC.state = CollectibleState.VANISHING;
            SpriteShape.copyShape(sparkleShape, shpC);
            if((movS != null) && (sound != null)) {
                sound.addSound(pickupSound, movC.position.x - movS.position.x);
            }
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
