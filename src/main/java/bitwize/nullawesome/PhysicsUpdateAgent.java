package bitwize.nullawesome;

import android.util.Log;

public class PhysicsUpdateAgent implements UpdateAgent {
    private EntityRepository repo;
    private EntityProcessor proc;

    private void doGrounded(SpriteMovement mov, WorldPhysics phys, TileMap map) {
	if(Math.abs(phys.gaccel) > 0.00001f) {
	    mov.acceleration.set(phys.gaccel, 0.f);
	    if(mov.velocity.x > phys.gvelmax) mov.velocity.x = phys.gvelmax;
	    if(mov.velocity.x < -phys.gvelmax) mov.velocity.x = -phys.gvelmax;
	}
	else {
	    if(Math.abs(mov.velocity.x) < 0.21f) { mov.velocity.x = 0.f; phys.gaccel = 0.f; }
	    else { mov.acceleration.set((mov.velocity.x > 0.f) ? -0.2f : 0.2f, 0.f); }
	}
	if((map.getTileFlags(map.getTileWorldCoords(mov.position.x, mov.position.y + phys.radius)) &
	    TileMap.FLAG_SOLID) == 0) {
	    phys.state = WorldPhysics.State.FALLING;
	}
    }
    private void doAir(SpriteMovement mov, WorldPhysics phys, TileMap map) {
	if(mov.velocity.y < phys.fallmax) {
	    mov.acceleration.set(phys.gravity);
	}
	else {
	    mov.acceleration.set(0.f, 0.f);
	}
	mov.acceleration.offset(phys.thrust.x, phys.thrust.y);
	if(mov.velocity.x > phys.gvelmax) mov.velocity.x = phys.gvelmax;
	if(mov.velocity.x < -phys.gvelmax) mov.velocity.x = -phys.gvelmax;
	if((map.getTileFlags(map.getTileWorldCoords(mov.position.x, mov.position.y + phys.radius)) &
	    TileMap.FLAG_SOLID) != 0) {
	    phys.state = WorldPhysics.State.GROUNDED;
	    mov.velocity.y = 0.f;
	    mov.acceleration.y = 0.f;
	    phys.thrust.set(0.f, 0.f);
	    mov.position.y -= ((int)(mov.position.y + phys.radius) % TileMap.TILE_SIZE);
	}
    }

    private void evictGround(SpriteMovement mov, WorldPhysics phys, TileMap map) {
	// Test for collision with a solid piece of ground on either
	// the left or right side and push the object out until the
	// test fails, stopping horizontal movement in the process.

	boolean collideLeft, collideRight;
	collideLeft = ((map.getTileFlags(map.getTileWorldCoords(mov.position.x - (phys.radius * 0.6f), mov.position.y)) &
			TileMap.FLAG_SOLID) != 0);
	collideRight = ((map.getTileFlags(map.getTileWorldCoords(mov.position.x + (phys.radius * 0.6f), mov.position.y)) &
			 TileMap.FLAG_SOLID) != 0);
	if(collideLeft || collideRight) {
	    mov.velocity.x = 0.f;
	    mov.acceleration.x = 0.f;
	    phys.gaccel = 0.f;
	    phys.thrust.set(0.f, 0.f);
	    float incr = collideRight ? -1.f : 1.f;
	    while(collideLeft || collideRight) {
		mov.position.x += incr;
		collideLeft = ((map.getTileFlags(map.getTileWorldCoords(mov.position.x - (phys.radius * 0.6f), mov.position.y)) &
				TileMap.FLAG_SOLID) != 0);
		collideRight = ((map.getTileFlags(map.getTileWorldCoords(mov.position.x + (phys.radius * 0.6f), mov.position.y)) &
				 TileMap.FLAG_SOLID) != 0);
		Log.i("evictGround", "left: " + collideLeft + " right: " + collideRight);
	    }
	}
    }
    public PhysicsUpdateAgent() {
	repo = EntityRepository.get();
	proc = new EntityProcessor() {
		public void process(int eid) {
		    SpriteMovement mov;
		    WorldPhysics phys;
		    StageInfo info = null;
		    try {
			mov = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
			phys = (WorldPhysics)repo.getComponent(eid, WorldPhysics.class);
			if(phys != null) info = (StageInfo)repo.getComponent(phys.stageEid, StageInfo.class);
		    }
		    catch(InvalidEntityException e) { return; }
		    if(mov == null) return;
		    if(phys != null) {
			if(info == null) return;
			switch(phys.state) {
			case GROUNDED:
			    doGrounded(mov, phys, info.map);
			    break;
			case FALLING:
			    doAir(mov, phys, info.map);
			    break;
			}
			evictGround(mov, phys, info.map);
		    }
		}
	    };
    }
    public void update(long time) {
	repo.processEntitiesWithComponent(WorldPhysics.class, proc);
    }
}
