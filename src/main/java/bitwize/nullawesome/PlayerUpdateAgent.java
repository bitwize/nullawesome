package bitwize.nullawesome;

import android.graphics.Bitmap;

public class PlayerUpdateAgent implements UpdateAgent {
    private EntityRepository repo = EntityRepository.get();
    private ContentRepository content = ContentRepository.get();
    private Bitmap rightBitmap;
    private Bitmap leftBitmap;
    public PlayerUpdateAgent() {
	rightBitmap = content.getBitmap("player_r");
	leftBitmap = content.getBitmap("player_l");
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
		phys.gaccel = 0.2f;
		phys.facingRight = true;
	    }
	    else if((pi.keyStatus & PlayerInfo.KEY_LEFT) != 0) {
		phys.gaccel = -0.2f;
		phys.facingRight = false;
	    }
	    if((pi.keyStatus & PlayerInfo.KEY_JUMP) != 0) {
		mov.position.y -= 2.0f;
		mov.velocity.y = -3.f;
		phys.gaccel = 0.f;
		mov.acceleration.x = 0.f;
		phys.state = WorldPhysics.State.FALLING;
	    }
	    else {
		phys.gaccel = 0.f;
	    }
	    shp.shapes = phys.facingRight ? rightBitmap : leftBitmap;
	}
	catch(InvalidEntityException e) {}
    }

}
