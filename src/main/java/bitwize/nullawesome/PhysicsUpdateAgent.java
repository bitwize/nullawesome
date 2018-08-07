package bitwize.nullawesome;

import android.util.Log;
import android.graphics.RectF;

public class PhysicsUpdateAgent implements UpdateAgent {
    private EntityRepository repo;
    private EntityProcessor proc;
    private RectF elevRect = new RectF();
    private void doGrounded(SpriteMovement mov, WorldPhysics phys, TileMap map) {
	boolean shouldFall;
	if(Math.abs(phys.gaccel) > 0.00001f) {
	    mov.acceleration.set(phys.gaccel, 0.f);
	    if(mov.velocity.x > phys.gvelmax) mov.velocity.x = phys.gvelmax;
	    if(mov.velocity.x < -phys.gvelmax) mov.velocity.x = -phys.gvelmax;
	}
	else {
	    if(Math.abs(mov.velocity.x) < 0.21f) { mov.velocity.x = 0.f; phys.gaccel = 0.f; }
	    else { mov.acceleration.set((mov.velocity.x > 0.f) ? -0.2f : 0.2f, 0.f); }
	}
	if(phys.currentElevatorEid == EntityRepository.NO_ENTITY) {
	    shouldFall = (map.getTileFlags(map.getTileWorldCoords(mov.position.x, mov.position.y + phys.radius)) &
			  TileMap.FLAG_SOLID) == 0;
	} else {
	    SpriteMovement movE = (SpriteMovement)repo.getComponent(phys.currentElevatorEid, SpriteMovement.class);
	    WorldPhysics physE = (WorldPhysics)repo.getComponent(phys.currentElevatorEid, WorldPhysics.class);
	    if(movE == null || physE == null) {
		shouldFall = true;
	    } else {
		elevRect.set(physE.hitbox);
		elevRect.offset(movE.position.x, movE.position.y);
		shouldFall = !(elevRect.contains(mov.position.x, mov.position.y + phys.radius));
	    }
	}
	if(shouldFall) {
	    phys.state = WorldPhysics.State.FALLING;
	    phys.currentElevatorEid = EntityRepository.NO_ENTITY;
	}
    }
    private void doAir(SpriteMovement mov, WorldPhysics phys, TileMap map) {
	phys.currentElevatorEid = EntityRepository.NO_ENTITY;
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
	if((map.getTileFlags(map.getTileWorldCoords(mov.position.x, mov.position.y - phys.radius)) &
	    TileMap.FLAG_SOLID) != 0) {
	    mov.velocity.y = 0.f;
	    mov.acceleration.y = 0.f;
	    phys.thrust.set(0.f, 0.f);
	    mov.position.y += TileMap.TILE_SIZE - ((int)(mov.position.y - phys.radius) % TileMap.TILE_SIZE);
	}
    }

    private void evictGround(SpriteMovement mov, WorldPhysics phys, TileMap map) {
	// Test for collision with a solid piece of ground on either
	// the left or right side and push the object out until the
	// test fails, stopping horizontal movement in the process.

	boolean collideLeft, collideRight;
	float offset = phys.radius * 0.6f;
	collideLeft = ((map.getTileFlags(map.getTileWorldCoords(mov.position.x - offset, mov.position.y)) &
			TileMap.FLAG_SOLID) != 0);
	collideRight = ((map.getTileFlags(map.getTileWorldCoords(mov.position.x + offset, mov.position.y)) &
			 TileMap.FLAG_SOLID) != 0);
	if((collideLeft && (mov.velocity.x < 0.f)) || (collideRight && (mov.velocity.x > 0.f))) {
	    mov.velocity.x = 0.f;
	    mov.acceleration.x = 0.f;
	    phys.gaccel = 0.f;
	    phys.thrust.set(0.f, 0.f);
	    if(collideLeft) {
		mov.position.x -= ((int)(mov.position.x - offset) % TileMap.TILE_SIZE);
		mov.position.x += TileMap.TILE_SIZE;
	    }
	    else {
		mov.position.x -= ((int)(mov.position.x + offset) % TileMap.TILE_SIZE);
	    }
	    /*
	    while(collideLeft || collideRight) {
		mov.position.x += incr;
		collideLeft = ((map.getTileFlags(map.getTileWorldCoords(mov.position.x - offset + 1, mov.position.y)) &
				TileMap.FLAG_SOLID) != 0);
		collideRight = ((map.getTileFlags(map.getTileWorldCoords(mov.position.x + offset - 1, mov.position.y)) &
				 TileMap.FLAG_SOLID) != 0);
	    }
	    mov.position.x -= incr;
	    */
	}
    }
    public PhysicsUpdateAgent() {
	repo = EntityRepository.get();
	proc = new EntityProcessor() {
		public void process(int eid) {
		    SpriteMovement mov;
		    WorldPhysics phys;
		    StageInfo info = null;
		    mov = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
		    phys = (WorldPhysics)repo.getComponent(eid, WorldPhysics.class);
		    if(mov == null || phys == null) return;
		    info = (StageInfo)repo.getComponent(phys.stageEid, StageInfo.class);
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
	    };
    }
    public void update(long time) {
	repo.processEntitiesWithComponent(WorldPhysics.class, proc);
    }
}
