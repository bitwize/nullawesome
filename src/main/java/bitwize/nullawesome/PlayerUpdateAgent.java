package bitwize.nullawesome;

import android.graphics.Bitmap;

public class PlayerUpdateAgent implements UpdateAgent {
    private EntityRepository repo = EntityRepository.get();
    private ContentRepository content = ContentRepository.get();
    private Bitmap rightBitmap;
    private Bitmap leftBitmap;
    private SpriteShape standShape;
    private SpriteShape walkShape;
    public PlayerUpdateAgent() {
	rightBitmap = content.getBitmap("player_r");
	leftBitmap = content.getBitmap("player_l");
	standShape = SpriteShape.loadAnimation(content.getAnimation("player_stand"));
	walkShape = SpriteShape.loadAnimation(content.getAnimation("player_walk"));
    }

    private void switchStanding(SpriteShape shp) {
	if(shp.frames == standShape.frames) return;
	shp.maxFrames = standShape.maxFrames;
	shp.frames = standShape.frames;
	shp.timings = standShape.timings;
	shp.currentFrame = 0;
	shp.currentTime = 0;
    }

    private void switchWalking(SpriteShape shp) {
	if(shp.frames == walkShape.frames) return;
	shp.maxFrames = standShape.maxFrames;
	shp.frames = walkShape.frames;
	shp.timings = walkShape.timings;
	shp.currentFrame = 0;
	shp.currentTime = 0;
    }

    public void update(long time) {
	int eid = repo.findEntityWithComponent(PlayerInfo.class);
	PlayerInfo pi;
	WorldPhysics phys;
	SpriteShape shp;
	SpriteMovement mov;
	try {
	    pi = (PlayerInfo)repo.getComponent(eid, PlayerInfo.class);
	    shp = (SpriteShape)repo.getComponent(eid, SpriteShape.class);
	    mov = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
	    phys = (WorldPhysics)repo.getComponent(eid, WorldPhysics.class);
	    if((pi.keyStatus & PlayerInfo.KEY_RIGHT) != 0) {
		switch(phys.state) {
		case GROUNDED:
		    phys.gaccel = 0.2f;
		    phys.facingRight = true;
		    break;
		case FALLING:
		    phys.thrust.x = 0.04f;
		    break;
		}
		switchWalking(shp);
	    }
	    else if((pi.keyStatus & PlayerInfo.KEY_LEFT) != 0) {
		switch(phys.state) {
		case GROUNDED:
		    phys.gaccel = -0.2f;
		    phys.facingRight = false;
		    break;
		case FALLING:
		    phys.thrust.x = -0.04f;
		    break;
		}
		switchWalking(shp);
	    }
	    else {
		phys.gaccel = 0.f;
		switchStanding(shp);
	    }
	    if((phys.state == WorldPhysics.State.GROUNDED)
	       && ((pi.keyStatus & PlayerInfo.KEY_JUMP) != 0)) {
		mov.position.y -= 2.0f;
		mov.velocity.y = -4.f;
		phys.gaccel = 0.f;
		mov.acceleration.x = 0.f;
		phys.state = WorldPhysics.State.FALLING;
	    }
	    shp.shapes = phys.facingRight ? rightBitmap : leftBitmap;
	}
	catch(InvalidEntityException e) {}
    }

}
