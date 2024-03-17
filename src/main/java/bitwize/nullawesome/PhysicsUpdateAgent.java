package bitwize.nullawesome;

import android.util.Log;
import android.graphics.RectF;

public class PhysicsUpdateAgent implements UpdateAgent {
    private EntityRepository repo;
    private EntityProcessor proc = (eid) -> {
        SpriteMovement mov;
        WorldPhysics phys;
        StageInfo info = null;
        mov = (SpriteMovement)repo.getComponent(eid, SpriteMovement.class);
        phys = (WorldPhysics)repo.getComponent(eid, WorldPhysics.class);
        if(mov == null || phys == null) return;
        info = (StageInfo)repo.getComponent(phys.stageEid, StageInfo.class);
        if(info == null) return;
        if((phys.flags & WorldPhysics.SOLID_COLLISION) != 0) {
            checkDeathFloor(mov, phys, info);
            doTouchdeath(mov, phys, info.map);
            switch(phys.state) {
            case GROUNDED:
                doGrounded(mov, phys, info.map);
                break;
            case FALLING:
                doAir(mov, phys, info.map);
                break;
            }
            evictGround(mov, phys, info.map);
        } else {
            doAir(mov, phys, info.map);
        }
    };
    private RelevantEntitiesHolder reh = new RelevantEntitiesHolder(RelevantEntitiesHolder.hasComponentCriterion(WorldPhysics.class));
    private RectF elevRect = new RectF();
    public static final float BASE_FRIC = 0.5f;

    // Handle all of the physics and movement stuff when the entity is
    // "on the ground" (state GROUNDED). It translates the entity's
    // ground velocity and acceleration into velocity and acceleration
    // in world coordinates, keeps the entity stuck to either the
    // ground or a moving sprite that counts as ground, and checks to
    // see whether it should start falling (because it either jumped
    // or walked off a ledge).
    
    private void doGrounded(SpriteMovement mov, WorldPhysics phys, TileMap map) {
        boolean shouldFall;
        if(Math.abs(phys.gaccel) > 0.00001f) {
            mov.acceleration.set(phys.gaccel, 0.f);
            if(mov.velocity.x > phys.gvelmax) mov.velocity.x = phys.gvelmax;
            if(mov.velocity.x < -phys.gvelmax) mov.velocity.x = -phys.gvelmax;
        }
        else {
            if(Math.abs(mov.velocity.x) < (BASE_FRIC + 0.01f)) {
                mov.velocity.x = 0.f; phys.gaccel = 0.f;
            } else {
                mov.acceleration.set((mov.velocity.x > 0.f) ? -BASE_FRIC  : BASE_FRIC, 0.f);
            }
        }
        if(phys.sticksToEid == EntityRepository.NO_ENTITY) {
            shouldFall = (map.getTileFlags(map.getTileWorldCoords(mov.position.x, mov.position.y + phys.radius)) &
                          TileMap.FLAG_SOLID) == 0;
        } else {
            SpriteMovement movE = (SpriteMovement)repo.getComponent(phys.sticksToEid, SpriteMovement.class);
            WorldPhysics physE = (WorldPhysics)repo.getComponent(phys.sticksToEid, WorldPhysics.class);
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
            phys.sticksToEid = EntityRepository.NO_ENTITY;
            phys.flags |= WorldPhysics.COYOTE_TIME;
            phys.coyoteTime = phys.maxCoyoteTime;
        }
    }

    // Handle the movement and physics when an entity is in the air
    // (state FALLING). It adds the entity's "thrust" and "gravity"
    // vectors to come up with a world-coordinate acceleration vector,
    // and checks to see if the entity collided with a ground tile.
    
    private void doAir(SpriteMovement mov, WorldPhysics phys, TileMap map) {
        phys.sticksToEid = EntityRepository.NO_ENTITY;
        if(mov.velocity.y < phys.fallmax) {
            mov.acceleration.set(phys.gravity);
        }
        else {
            mov.acceleration.set(0.f, 0.f);
        }
        mov.acceleration.offset(phys.thrust.x, phys.thrust.y);
        if(mov.velocity.x > phys.gvelmax) mov.velocity.x = phys.gvelmax;
        if(mov.velocity.x < -phys.gvelmax) mov.velocity.x = -phys.gvelmax;
        if((phys.flags & WorldPhysics.COYOTE_TIME) != 0) {
            phys.coyoteTime--;
            if(phys.coyoteTime <= 0) {
                phys.flags &= ~WorldPhysics.COYOTE_TIME;
            }
        }
        if((phys.flags & WorldPhysics.SOLID_COLLISION) == 0) { return; }
        // tile collision stuff. We skip over this if the SOLID_COLLISION flag is off.
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

    // Determine whether the entity should "die" upon touching a
    // deadly tile.
    
    private void doTouchdeath(SpriteMovement mov, WorldPhysics phys, TileMap map) {
        if(
           ((map.getTileFlags(map.getTileWorldCoords(mov.position.x, mov.position.y - phys.radius)) &
             TileMap.FLAG_TOUCHDEATH) != 0) ||
           ((map.getTileFlags(map.getTileWorldCoords(mov.position.x, mov.position.y + phys.radius)) &
             TileMap.FLAG_TOUCHDEATH) != 0)
           ) {
            phys.flags |= WorldPhysics.SHOULD_DESTROY;
        }
    }
    
    // Test for collision with a solid piece of ground on either the
    // left or right side and push the object out until the test
    // fails, stopping horizontal movement in the process.
    
    private void evictGround(SpriteMovement mov, WorldPhysics phys, TileMap map) {

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

    // Check to see whether the entity has fallen below the level's
    // "death floor" (the Y-coordinate beyond which objects are
    // considered to have irretrievably fallen into a pit).

    private void checkDeathFloor(SpriteMovement mov,
                                 WorldPhysics phys,
                                 StageInfo info) {
        if(mov.position.y > info.deathFloorY) {
            phys.flags |= WorldPhysics.SHOULD_DESTROY;
        } else {
            phys.flags &= ~WorldPhysics.SHOULD_DESTROY;
        }
    }
    public PhysicsUpdateAgent() {
        repo = EntityRepository.get();
        reh.register();
    }
    public void update(long time) {
        reh.processAll(proc);
    }
}
