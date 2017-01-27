package bitwize.nullawesome;

import android.graphics.Bitmap;

public class PlayerUpdateAgent implements UpdateAgent {
    private EntityRepository repo = EntityRepository.get();
    private ContentRepository content = ContentRepository.get();
    private Bitmap rightBitmap;
    private Bitmap leftBitmap;
    private SpriteShape standShape;
    private SpriteShape walkShape;
    private SpriteShape jumpShape;
    private int player_eid;
    public PlayerUpdateAgent(int eid) {
	player_eid = eid;
	rightBitmap = content.getBitmap("player_r");
	leftBitmap = content.getBitmap("player_l");
	standShape = SpriteShape.loadAnimation(content.getAnimation("player_stand"));
	walkShape = SpriteShape.loadAnimation(content.getAnimation("player_walk"));
	jumpShape = SpriteShape.loadAnimation(content.getAnimation("player_jump"));
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
	shp.maxFrames = walkShape.maxFrames;
	shp.frames = walkShape.frames;
	shp.timings = walkShape.timings;
	shp.currentFrame = 0;
	shp.currentTime = 0;
    }

    private void switchJumping(SpriteShape shp) {
	if(shp.frames == jumpShape.frames) return;
	shp.maxFrames = jumpShape.maxFrames;
	shp.frames = jumpShape.frames;
	shp.timings = jumpShape.timings;
	shp.currentFrame = 0;
	shp.currentTime = 0;
    }

    public void update(long time) {
	int eid = player_eid;
	PlayerInfo pi;
	WorldPhysics phys;
	SpriteShape shp;
	SpriteMovement mov;
	pi = (PlayerInfo)repo.getComponent(eid, PlayerInfo.class);
	shp = (SpriteShape)repo.getComponent(eid, SpriteShape.class);
	mov = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
	phys = (WorldPhysics)repo.getComponent(eid, WorldPhysics.class);
	if(pi == null || shp == null || mov == null || phys == null) return;
	if((pi.keyStatus & PlayerInfo.KEY_RIGHT) != 0) {
	    switch(phys.state) {
	    case GROUNDED:
		phys.gaccel = 0.2f;
		phys.facingRight = true;
		switchWalking(shp);
		break;
	    case FALLING:
		phys.thrust.x = 0.04f;
		break;
	    }
	}
	else if((pi.keyStatus & PlayerInfo.KEY_LEFT) != 0) {
	    switch(phys.state) {
	    case GROUNDED:
		phys.gaccel = -0.2f;
		phys.facingRight = false;
		switchWalking(shp);
		break;
	    case FALLING:
		phys.thrust.x = -0.04f;
		break;
	    }
	}
	else if(phys.state == WorldPhysics.State.GROUNDED) {
	    phys.gaccel = 0.f;
	    switchStanding(shp);
	}
	
	// The JUMPED PlayerInfo flag tracks whether we've already
	// jumped for this jump key press. A jump will not be
	// triggered if the player is still holding the jump button
	// down from his last jump. This prevents Lorn from having
	// "moon boots" (i.e., he bounces continuously as long as the
	// jump button is held).

	if((phys.state == WorldPhysics.State.GROUNDED)
	   && ((pi.keyStatus & PlayerInfo.KEY_JUMP) != 0)
	   && ((pi.flags & PlayerInfo.JUMPED) == 0)) {
	    mov.position.y -= 2.0f;
	    mov.velocity.y = -6.f;
	    phys.gaccel = 0.f;
	    mov.acceleration.x = 0.f;
	    phys.state = WorldPhysics.State.FALLING;
	    pi.flags |= PlayerInfo.JUMPED;
	    switchJumping(shp);
	}

	// If the jump key is released, reset the JUMPED flag.

	if((pi.keyStatus & PlayerInfo.KEY_JUMP) == 0) {
	    pi.flags &= (~PlayerInfo.JUMPED);
	}
	shp.shapes = phys.facingRight ? rightBitmap : leftBitmap;
    }
}
